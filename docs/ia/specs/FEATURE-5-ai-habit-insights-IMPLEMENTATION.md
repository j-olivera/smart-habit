# Feature 5 — AI Weekly Report Generator (Ollama Integration)

> **Estado**: ✅ Implementado y testeado  
> **Fecha de implementación**: 2026-04-26  
> **Versión**: 1.0.0

---

## 1. Overview

La Feature 5 es el módulo de **generación de reportes semanales mediante IA local**. Utiliza **Ollama** con el modelo `phi3` para analizar los registros diarios de hábitos del usuario y generar un reporte personalizado con 5 secciones fijas:

1. **RESUMEN GENERAL** — 2-3 oraciones sobre la semana
2. **FORTALEZAS** — hasta 3 puntos concretos
3. **ÁREAS DE MEJORA** — hasta 3 puntos accionables
4. **RIESGOS DETECTADOS** — patrones preocupantes o "Sin riesgos detectados"
5. **RECOMENDACIONES** — hasta 3 sugerencias para la próxima semana

### Características principales

- **Trigger automático**: Scheduler los domingos a las 23:59
- **Trigger on-demand**: Endpoint HTTP `POST /api/reports/weekly/generate`
- **Mínimo 3 días**: Se requieren al menos 3 DailyEntry para generar el reporte
- **Upsert**: Si existe reporte para la semana, se sobreescribe
- **Privacidad**: Todo corre localmente con Ollama, sin datos externos

---

## 2. Dependencias agregadas

### 2.1 pom.xml

Se agregaron las siguientes dependencias para la integración con Spring AI y Ollama:

```xml
<!-- Versiones -->
<properties>
    <spring-ai.version>1.1.2</spring-ai.version>
</properties>

<!-- Repository para Spring Milestones -->
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>

<!-- BOM para gestión de versiones -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Starter de Ollama -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

### 2.2 application.yml

Configuración de Spring AI Ollama:

```yaml
spring.ai.ollama.base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
spring.ai.ollama.chat.options.model: phi3
spring.ai.ollama.chat.options.temperature: 0.3
spring.ai.ollama.chat.options.num-predict: 800
```

**Explicación de configuraciones**:
- `base-url`: URL de Ollama (en Docker usa el nombre del servicio)
- `model`: Modelo `phi3` de Microsoft (eficiente en CPU, <2GB RAM)
- `temperature`: 0.3 para respuestas deterministas (menos alucinaciones)
- `num_predict`: Límite de tokens en la respuesta

### 2.3 docker-compose.yml

El servicio Ollama ya estaba configurado previamente:

```yaml
ollama:
    image: ollama/ollama:latest
    container_name: smart-ollama
    ports:
        - "${OLLAMA_PORT:-11434}:11434"
    volumes:
        - ollama-data:/root/.ollama
    healthcheck:
        test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
```

**Nota**: Para que el modelo esté disponible al iniciar, se puede agregar un script de entrypoint:

```bash
# ollama-entrypoint.sh
#!/bin/bash
ollama serve &
sleep 5
ollama pull phi3 2>/dev/null || true
wait
```

---

## 3. Capa de Dominio (Domain)

### 3.1 Entidad: WeeklyReport

**Ubicación**: `domain/model/report/WeeklyReport.java`

```java
public class WeeklyReport {
    private Long id;
    private Long userId;
    private LocalDate weekStart;      // Lunes de la semana
    private LocalDate weekEnd;        // Domingo de la semana
    private String aiContent;         // Contenido generado por IA
    private Instant generatedAt;      // Timestamp de generación

    // Métodos factory y update
    public static WeeklyReport create(Long userId, LocalDate weekStart, LocalDate weekEnd, String aiContent)
    public WeeklyReport update(String aiContent)
}
```

**Tabla existente**: `weekly_reports` (V5 migration ya existía)

```sql
CREATE TABLE weekly_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start DATE NOT NULL,
    week_end DATE NOT NULL,
    ai_content TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, week_start)
);
```

### 3.2 Excepciones de dominio

| Excepción | HTTP | Descripción |
|-----------|------|-------------|
| `InsufficientDataException` | 422 | Menos de 3 días con logs |
| `ReportGenerationException` | 503 | Ollama no disponible o timeout |

**InsufficientDataException** (`domain/exception/InsufficientDataException.java`):
```java
public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException(int daysFound, int minRequired) {
        super("Se necesitan al menos %d días registrados. Días encontrados: %d"
            .formatted(minRequired, daysFound));
    }
}
```

**ReportGenerationException** (`domain/exception/ReportGenerationException.java`):
```java
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## 4. Capa de Aplicación (Application)

### 4.1 Puertos de Entrada (Input Ports)

#### GenerateWeeklyReportPort

**Ubicación**: `application/port/in/report/GenerateWeeklyReportPort.java`

```java
public interface GenerateWeeklyReportPort {
    WeeklyReportResult execute(GenerateWeeklyReportCommand command);
}
```

#### GenerateWeeklyReportCommand

**Ubicación**: `application/port/in/report/GenerateWeeklyReportCommand.java`

```java
public record GenerateWeeklyReportCommand(
    Long userId,
    LocalDate weekStart,
    String triggeredBy   // "SCHEDULER" | "USER"
) {}
```

### 4.2 Puertos de Salida (Output Ports)

#### AiAssistantPort

**Ubicación**: `application/port/out/ai/AiAssistantPort.java`

```java
public interface AiAssistantPort {
    String generateWeeklyInsight(String structuredPrompt);
}
```

**Propósito**: Abstraer la implementación de IA. El use case no sabe que existe Ollama.

#### PromptBuilderPort

**Ubicación**: `application/port/out/ai/PromptBuilderPort.java`

```java
public interface PromptBuilderPort {
    String build(List<DailyEntryWithLogsResult> entries, LocalDate weekStart, LocalDate weekEnd);
}
```

**Propósito**: Construir el prompt estructurado que se envía al modelo.

#### WeeklyReportRepositoryPort

**Ubicación**: `application/port/out/report/WeeklyReportRepositoryPort.java`

```java
public interface WeeklyReportRepositoryPort {
    Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    WeeklyReport save(WeeklyReport report);
}
```

### 4.3 DTOs

#### WeeklyReportResult

**Ubicación**: `application/dto/report/WeeklyReportResult.java`

```java
public record WeeklyReportResult(
    Long id,
    LocalDate weekStart,
    LocalDate weekEnd,
    String aiContent,
    Instant generatedAt
) {}
```

#### WeeklyReportResponse

**Ubicación**: `application/dto/report/WeeklyReportResponse.java`

```java
public record WeeklyReportResponse(
    Long id,
    LocalDate weekStart,
    LocalDate weekEnd,
    String aiContent,
    Instant generatedAt
) {
    public static WeeklyReportResponse from(WeeklyReportResult result) {
        return new WeeklyReportResponse(...);
    }
}
```

### 4.4 Use Case: GenerateWeeklyReportUseCase

**Ubicación**: `application/usecase/report/GenerateWeeklyReportUseCase.java`

```java
@Service
@RequiredArgsConstructor
public class GenerateWeeklyReportUseCase implements GenerateWeeklyReportPort {

    private static final int MIN_DAYS_REQUIRED = 3;

    private final GetWeeklyEntriesUseCase getWeeklyEntriesUseCase;
    private final AiAssistantPort aiAssistant;
    private final WeeklyReportRepositoryPort reportRepo;
    private final PromptBuilderPort promptBuilder;

    @Override
    public WeeklyReportResult execute(GenerateWeeklyReportCommand cmd) {
        // 1. Normalizar weekStart al lunes
        LocalDate weekStart = cmd.weekStart().with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        // 2. Obtener entradas de la semana
        var weeklyReport = getWeeklyEntriesUseCase.execute(cmd.userId(), weekStart);
        List<DailyEntryWithLogsResult> entries = weeklyReport.dailyEntries();

        // 3. Validar mínimo de días
        if (entries.size() < MIN_DAYS_REQUIRED) {
            throw new InsufficientDataException(entries.size(), MIN_DAYS_REQUIRED);
        }

        // 4. Construir prompt
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // 5. Llamar al modelo
        String aiContent = aiAssistant.generateWeeklyInsight(prompt);

        // 6. Upsert del reporte
        WeeklyReport report = reportRepo
            .findByUserIdAndWeekStart(cmd.userId(), weekStart)
            .map(existing -> existing.update(aiContent))
            .orElseGet(() -> WeeklyReport.create(cmd.userId(), weekStart, weekEnd, aiContent));

        // 7. Persistir y retornar
        WeeklyReport saved = reportRepo.save(report);
        return new WeeklyReportResult(...);
    }
}
```

---

## 5. Capa de Infraestructura (Infrastructure)

### 5.1 Adaptador Ollama (AI Adapter)

**Ubicación**: `infrastructure/ai/OllamaAiAdapter.java`

```java
@Component
public class OllamaAiAdapter implements AiAssistantPort {

    private final ChatModel chatModel;

    public OllamaAiAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateWeeklyInsight(String structuredPrompt) {
        try {
            return chatModel.call(new Prompt(structuredPrompt))
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (Exception e) {
            throw new ReportGenerationException(
                "Error al conectar con Ollama: " + e.getMessage(), e);
        }
    }
}
```

**Detalles técnicos**:
- `ChatModel` es inyectado automáticamente por Spring AI mediante auto-configuración
- El método `getText()` extrae el contenido de la respuesta del modelo
- Cualquier excepción de Ollama se convierte en `ReportGenerationException` (503)

### 5.2 PromptBuilder

**Ubicación**: `infrastructure/ai/PromptBuilder.java`

```java
@Component
public class PromptBuilder implements PromptBuilderPort {

    private static final String SYSTEM_PROMPT = """
        Eres un coach de hábitos saludables, empático y motivador.
        Se te proporcionan los registros diarios de hábitos de un usuario durante una semana.
        
        Tu tarea es generar un reporte semanal con EXACTAMENTE estas secciones,
        usando los títulos tal cual están escritos:
        
        ## RESUMEN GENERAL
        (2-3 oraciones describiendo cómo fue la semana en general)
        
        ## FORTALEZAS
        (máximo 3 puntos concretos basados en los datos)
        
        ## ÁREAS DE MEJORA
        (máximo 3 puntos concretos y accionables)
        
        ## RIESGOS DETECTADOS
        (si los hay — patrones preocupantes como poco sueño sostenido, inactividad, etc.)
        (si no hay riesgos, escribir: "Sin riesgos detectados esta semana.")
        
        ## RECOMENDACIONES
        (máximo 3 recomendaciones concretas y personalizadas para la próxima semana)
        
        REGLAS:
        - Responde SIEMPRE en español.
        - Sé específico con los datos provistos. No inventes información.
        - Si un hábito no aparece registrado un día, NO asumas que no se realizó.
        - No uses lenguaje clínico ni alarmista.
        - No salgas del formato de secciones indicado.
        """;

    @Override
    public String build(List<DailyEntryWithLogsResult> entries, 
                       LocalDate weekStart, LocalDate weekEnd) {
        StringBuilder data = new StringBuilder();
        data.append("Semana del %s al %s:\n\n".formatted(weekStart, weekEnd));

        for (DailyEntryWithLogsResult entry : entries) {
            data.append("--- %s ---\n".formatted(entry.date()));
            serializeStudy(entry.studyLogs(), data);
            serializeExercise(entry.exerciseLogs(), data);
            serializeNutrition(entry.nutritionLogs(), data);
            serializeMood(entry.moodLogs(), data);
            serializeSleep(entry.sleepLogs(), data);
            data.append("\n");
        }

        return SYSTEM_PROMPT + "\n\nDATOS DEL USUARIO:\n" + data;
    }
    
    // Métodos de serialización para cada tipo de log...
}
```

**Ejemplo de prompt generado**:

```
Semana del 2026-04-20 al 2026-04-26:

--- 2026-04-21 ---
Estudio: Sí — 2.0hs de Java
Ejercicio: Sí — 1.0hs, CHEST, energía 80%
Alimentación: GOOD — cumplió objetivo
Ánimo: HAPPY
Sueño: 7.5hs (GOOD)

--- 2026-04-22 ---
Estudio: No — cansado
...
```

### 5.3 JPA Entity

**Ubicación**: `infrastructure/model/report/WeeklyReportJpaEntity.java`

```java
@Entity
@Table(name = "weekly_reports")
public class WeeklyReportJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "week_end", nullable = false)
    private LocalDate weekEnd;

    @Column(name = "ai_content", nullable = false, columnDefinition = "TEXT")
    private String aiContent;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
    
    // Getters, setters, constructores...
}
```

### 5.4 Repository

**Ubicación**: `infrastructure/repository/JpaWeeklyReportRepository.java`

```java
@Repository
public interface JpaWeeklyReportRepository extends JpaRepository<WeeklyReportJpaEntity, Long> {
    Optional<WeeklyReportJpaEntity> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
```

### 5.5 Repository Adapter

**Ubicación**: `infrastructure/adapter/report/WeeklyReportRepositoryAdapter.java`

```java
@Component
public class WeeklyReportRepositoryAdapter implements WeeklyReportRepositoryPort {

    private final JpaWeeklyReportRepository repository;

    @Override
    public Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart) {
        return repository.findByUserIdAndWeekStart(userId, weekStart)
                .map(this::toDomain);
    }

    @Override
    public WeeklyReport save(WeeklyReport report) {
        WeeklyReportJpaEntity entity = toEntity(report);
        WeeklyReportJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }
    
    // Métodos de mapeo between domain <-> entity
}
```

### 5.6 Scheduler

**Ubicación**: `infrastructure/scheduler/WeeklyReportScheduler.java`

```java
@Component
public class WeeklyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeeklyReportScheduler.class);

    private final GenerateWeeklyReportPort generateReport;
    private final JpaUserRepository userRepository;

    @Scheduled(cron = "59 23 * * SUN") // Domingos 23:59
    public void generateReportsForAllUsers() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        log.info("Starting scheduled weekly report generation for week: {}", weekStart);

        userRepository.findByActiveTrue().forEach(user ->
            CompletableFuture.runAsync(() -> {
                try {
                    generateReport.execute(new GenerateWeeklyReportCommand(
                        user.getId(), weekStart, "SCHEDULER"));
                } catch (Exception e) {
                    log.error("Failed to generate report for user {}: {}", 
                        user.getId(), e.getMessage());
                }
            })
        );
    }
}
```

**Características**:
- Corre los domingos a las 23:59
- Itera sobre todos los usuarios activos
- Ejecución asíncrona con `CompletableFuture.runAsync()`
- Manejo de errores por usuario (no rompe el batch completo)

### 5.7 Configuración de Use Cases

**Ubicación**: `infrastructure/config/ReportUseCaseConfig.java`

```java
@Configuration
public class ReportUseCaseConfig {

    @Bean
    public GenerateWeeklyReportPort generateWeeklyReportUseCase(
            GetWeeklyEntriesUseCase getWeeklyEntriesUseCase,
            AiAssistantPort aiAssistant,
            WeeklyReportRepositoryPort reportRepo,
            PromptBuilderPort promptBuilder) {
        return new GenerateWeeklyReportUseCase(
                getWeeklyEntriesUseCase,
                aiAssistant,
                reportRepo,
                promptBuilder
        );
    }
}
```

### 5.8 Habilitación de Scheduling

**Ubicación**: `SmartBackendApplication.java`

```java
@SpringBootApplication
@EnableScheduling
public class SmartBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartBackendApplication.class, args);
    }
}
```

### 5.9 Modificación de JpaUserRepository

Se agregó un método para encontrar usuarios activos:

```java
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByActiveTrue();  // NUEVO
}
```

---

## 6. Capa de Presentación (Presentation)

### 6.1 Controller

**Ubicación**: `infrastructure/controller/WeeklyReportController.java`

```java
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final GenerateWeeklyReportPort generateWeeklyReportUseCase;

    @PostMapping("/weekly/generate")
    public ResponseEntity<WeeklyReportResponse> generateWeeklyReport(
            @AuthenticationPrincipal User user) {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        var result = generateWeeklyReportUseCase.execute(
                new GenerateWeeklyReportCommand(user.getId(), weekStart, "USER"));
        return new ResponseEntity<>(WeeklyReportResponse.from(result), HttpStatus.OK);
    }
}
```

### 6.2 Manejo de Excepciones

**Ubicación**: `infrastructure/exception/GlobalExceptionHandler.java`

Se agregaron handlers para las nuevas excepciones:

```java
@ExceptionHandler(InsufficientDataException.class)
public ResponseEntity<Map<String, Object>> handleInsufficientDataException(InsufficientDataException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value()); // 422
    error.put("message", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
}

@ExceptionHandler(ReportGenerationException.class)
public ResponseEntity<Map<String, Object>> handleReportGenerationException(ReportGenerationException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.SERVICE_UNAVAILABLE.value()); // 503
    error.put("message", "El servicio de IA no está disponible temporalmente. Intenta más tarde.");
    return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
}
```

### 6.3 Respuesta de API

**Endpoint**: `POST /api/reports/weekly/generate`  
**Auth**: Bearer JWT

**Response 200**:
```json
{
  "id": 1,
  "weekStart": "2026-04-20",
  "weekEnd": "2026-04-26",
  "aiContent": "## RESUMEN GENERAL\nEsta semana fue muy productiva...",
  "generatedAt": "2026-04-26T23:59:01Z"
}
```

**Response 422** (datos insuficientes):
```json
{
  "timestamp": "2026-04-26T23:58:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Se necesitan al menos 3 días registrados. Días encontrados: 2"
}
```

**Response 503** (Ollama no disponible):
```json
{
  "timestamp": "2026-04-26T23:58:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "message": "El servicio de IA no está disponible temporalmente. Intenta más tarde."
}
```

---

## 7. Tests

### 7.1 GenerateWeeklyReportUseCaseTest

**Ubicación**: `src/test/java/com/smart/smart_backend/application/usecase/report/GenerateWeeklyReportUseCaseTest.java`

| Test | Descripción |
|------|-------------|
| `shouldGenerateWeeklyReportSuccessfully` | Genera reporte con 3+ días |
| `shouldThrowInsufficientDataExceptionWhenLessThan3Days` | Lanza 422 con menos de 3 días |
| `shouldUpdateExistingReport` | Actualiza reporte existente (upsert) |

### 7.2 PromptBuilderTest

**Ubicación**: `src/test/java/com/smart/smart_backend/infrastructure/ai/PromptBuilderTest.java`

| Test | Descripción |
|------|-------------|
| `shouldBuildPromptWithAllSections` | Verifica todas las secciones del prompt |
| `shouldHandleSkippedHabits` | Maneja hábitos omitidos (skipReason) |
| `shouldHandleEmptyLogs` | Maneja logs vacíos |
| `shouldHandleNapData` | Maneja datos de siesta |

### 7.3 WeeklyReportControllerTest

**Ubicación**: `src/test/java/com/smart/smart_backend/infrastructure/controller/WeeklyReportControllerTest.java`

| Test | Descripción |
|------|-------------|
| `shouldGenerateWeeklyReport` | Endpoint retorna 200 con reporte |
| `shouldReturn422WhenInsufficientData` | Endpoint retorna 422 cuando hay pocos datos |

---

## 8. Arquitectura Hexagonal

La implementación sigue estrictamente la **Arquitectura Hexagonal**:

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION                              │
│  WeeklyReportController → WeeklyReportResponse                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        APPLICATION                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐  │
│  │  Input Ports    │  │   Use Cases     │  │  Output Ports  │  │
│  │ GenerateWeekly  │──│ GenerateWeekly  │──│ AiAssistant    │  │
│  │ ReportPort      │  │ ReportUseCase   │  │ PromptBuilder  │  │
│  │ Command         │  │                 │  │ WeeklyReport   │  │
│  └─────────────────┘  └─────────────────┘  │ Repository     │  │
│                                            └────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐  │
│  │  Ollama Adapter │  │ PromptBuilder   │  │ JPA Repository │  │
│  │  (Spring AI)    │  │                 │  │ + Adapter      │  │
│  └─────────────────┘  └─────────────────┘  └────────────────┘  │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐                      │
│  │  Scheduler      │  │  WeeklyReport   │                      │
│  │  (Cron job)     │  │  JPA Entity     │                      │
│  └─────────────────┘  └─────────────────┘                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          DOMAIN                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐  │
│  │  WeeklyReport   │  │ Insufficient    │  │ Report         │  │
│  │  Entity         │  │ DataException   │  │ Generation     │  │
│  │                 │  │                 │  │ Exception      │  │
│  └─────────────────┘  └─────────────────┘  └────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 9. Pasos de Implementación

### Paso 1: Dependencias
1. Agregar Spring AI BOM al `pom.xml`
2. Agregar dependencia `spring-ai-starter-model-ollama`
3. Configurar `application.yml` con settings de Ollama
4. Habilitar `@EnableScheduling` en la aplicación principal

### Paso 2: Capa de Dominio
1. Crear entidad `WeeklyReport` en `domain/model/report/`
2. Crear excepciones `InsufficientDataException` y `ReportGenerationException`

### Paso 3: Capa de Aplicación (Puertos)
1. Crear puerto de entrada `GenerateWeeklyReportPort`
2. Crear Command `GenerateWeeklyReportCommand`
3. Crear puertos de salida `AiAssistantPort`, `PromptBuilderPort`, `WeeklyReportRepositoryPort`

### Paso 4: Capa de Aplicación (Use Case)
1. Crear DTOs `WeeklyReportResult`, `WeeklyReportResponse`
2. Implementar `GenerateWeeklyReportUseCase`

### Paso 5: Capa de Infraestructura (AI)
1. Crear `OllamaAiAdapter` implementando `AiAssistantPort`
2. Crear `PromptBuilder` implementando `PromptBuilderPort`

### Paso 6: Capa de Infraestructura (Persistence)
1. Crear `WeeklyReportJpaEntity`
2. Crear `JpaWeeklyReportRepository`
3. Crear `WeeklyReportRepositoryAdapter`
4. Agregar método `findByActiveTrue()` a `JpaUserRepository`

### Paso 7: Capa de Infraestructura (Scheduler)
1. Crear `WeeklyReportScheduler` con `@Scheduled`
2. Crear `ReportUseCaseConfig` para wiring

### Paso 8: Capa de Presentación
1. Crear `WeeklyReportController`
2. Agregar handlers de excepciones al `GlobalExceptionHandler`

### Paso 9: Tests
1. Crear `GenerateWeeklyReportUseCaseTest`
2. Crear `PromptBuilderTest`
3. Crear `WeeklyReportControllerTest`

### Paso 10: Verificación
1. Compilar: `./mvnw compile` ✅
2. Tests: `./mvnw test` ✅

---

## 10. Cómo probarlo

### 10.1 Requisitos previos

1. **Ollama instalado y corriendo**:
   ```bash
   # En local (no Docker)
   ollama serve
   ollama pull phi3
   ```

2. **Backend iniciado** con las variables de entorno:
   ```bash
   export OLLAMA_BASE_URL=http://localhost:11434
   ./mvnw spring-boot:run
   ```

### 10.2 Llamar al endpoint

```bash
# Obtener token JWT primero (login)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password"}'

# Llamar al endpoint de reporte
curl -X POST http://localhost:8080/api/reports/weekly/generate \
  -H "Authorization: Bearer $TOKEN"
```

### 10.3 Verificar scheduler

El scheduler corre automáticamente los domingos a las 23:59. Para probar manualmente:

```bash
# Modificar temporalmente la anotación @Scheduled
# o invocar el método directamente desde código
```

---

## 11. Troubleshooting

### Ollama no conecta

**Síntoma**: `ReportGenerationException: Error al conectar con Ollama`

**Soluciones**:
1. Verificar que Ollama esté corriendo: `curl http://localhost:11434/api/tags`
2. Verificar que el modelo `phi3` esté descargado: `ollama list`
3. En Docker, verificar network: ambos servicios deben estar en `smart-internal`

### Memoria insuficiente

**Síntoma**: Ollama se cuelga o el contenedor se reinicia

**Solución**: Usar modelo más ligero como `qwen:1.5b` en lugar de `phi3`

### Tiempo de respuesta lento

**Síntoma**: El endpoint tarda más de 30 segundos

**Solución**: 
- Frontend debe mostrar skeleton loader
- Considerar hacer la llamada asíncrona
- Reducir `num_predict` en application.yml

---

## 12. Archivos creados/modificados

### Nuevos archivos

| Capa | Archivo |
|------|---------|
| Domain | `domain/model/report/WeeklyReport.java` |
| Domain | `domain/exception/InsufficientDataException.java` |
| Domain | `domain/exception/ReportGenerationException.java` |
| Application (Port) | `application/port/in/report/GenerateWeeklyReportPort.java` |
| Application (Port) | `application/port/in/report/GenerateWeeklyReportCommand.java` |
| Application (Port) | `application/port/out/ai/AiAssistantPort.java` |
| Application (Port) | `application/port/out/ai/PromptBuilderPort.java` |
| Application (Port) | `application/port/out/report/WeeklyReportRepositoryPort.java` |
| Application (Port) | `application/port/out/habit/WeeklyEntriesPort.java` |
| Application (DTO) | `application/dto/report/WeeklyReportResult.java` |
| Application (DTO) | `application/dto/report/WeeklyReportResponse.java` |
| Application (UseCase) | `application/usecase/report/GenerateWeeklyReportUseCase.java` |
| Infrastructure (AI) | `infrastructure/ai/OllamaAiAdapter.java` |
| Infrastructure (AI) | `infrastructure/ai/PromptBuilder.java` |
| Infrastructure (JPA) | `infrastructure/model/report/WeeklyReportJpaEntity.java` |
| Infrastructure (Repo) | `infrastructure/repository/JpaWeeklyReportRepository.java` |
| Infrastructure (Adapter) | `infrastructure/adapter/report/WeeklyReportRepositoryAdapter.java` |
| Infrastructure (Scheduler) | `infrastructure/scheduler/WeeklyReportScheduler.java` |
| Infrastructure (Config) | `infrastructure/config/ReportUseCaseConfig.java` |
| Presentation | `infrastructure/controller/WeeklyReportController.java` |
| Test | `src/test/java/.../GenerateWeeklyReportUseCaseTest.java` |
| Test | `src/test/java/.../PromptBuilderTest.java` |
| Test | `src/test/java/.../WeeklyReportControllerTest.java` |

### Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `pom.xml` | Agregadas dependencias Spring AI Ollama |
| `application.yml` | Configuración de Ollama |
| `SmartBackendApplication.java` | Agregado `@EnableScheduling` |
| `JpaUserRepository.java` | Agregado método `findByActiveTrue()` |
| `GlobalExceptionHandler.java` | Agregados handlers 422 y 503 |

---

## 13. Próximos pasos (Future Work)

- [ ] **Test de integración** con Ollama real
- [ ] **Endpoint GET** para obtener reportes existentes
- [ ] **Historial de reportes** por usuario
- [ ] **Email notifications** cuando el reporte está listo
- [ ] **Retry logic** para fallos de Ollama
- [ ] **Cacheo de prompts** si el usuario pide el mismo reporte

---

## 14. Referencias

- [Spring AI Ollama Documentation](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Ollama Models](https://github.com/ollama/ollama/tree/main/README.md)
- [Feature Spec](./FEATURE-5-ai-habit-insights.md)

---

*Documento generado automáticamente el 2026-04-26*