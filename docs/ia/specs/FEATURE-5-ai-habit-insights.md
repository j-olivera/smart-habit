# Feature 5 — ia-report-generator-backend
> **Versión:** 2.0 — Completa con trigger, prompt, persistencia, validaciones y arquitectura hexagonal  
> **Reemplaza:** FEATURE-ai-habit-insights.md (v1)  
> **Cambios clave:** trigger dual (Scheduler + on-demand), system prompt definido, política de persistencia, mínimo de días, serialización del input al modelo

---

## 1. Contexto y Objetivo

Generar un reporte personalizado semanal analizando los `DailyEntry` + logs del usuario
mediante un modelo de lenguaje local (Ollama). El reporte se guarda en `weekly_reports`
y se envía por email. El usuario también puede solicitarlo manualmente desde el frontend.

**Decisión de privacidad:** se descartan APIs externas (OpenAI, Claude, etc.).
Todo corre en la misma infraestructura Docker, sin datos saliendo a terceros.

---

## 2. Infraestructura Docker

Se extiende el `docker-compose.yml` con un servicio dedicado:

```yaml
services:
  ollama:
    image: ollama/ollama:latest
    container_name: smart-ollama
    volumes:
      - ollama_data:/root/.ollama          # persiste el modelo descargado
      - ./ollama-entrypoint.sh:/entrypoint.sh
    entrypoint: ["/entrypoint.sh"]         # pull automático si no existe
    networks:
      - smart-network                       # mismo bridge que el backend
    # SIN ports expuestos — solo el backend puede hablar con Ollama

volumes:
  ollama_data:
```

**`ollama-entrypoint.sh`**
```bash
#!/bin/bash
ollama serve &
sleep 5
ollama pull phi3 2>/dev/null || true      # no falla si ya existe
wait
```

**Modelo elegido:** `phi3` (Microsoft) — eficiente en CPU ARM, <2GB RAM,
respuestas coherentes en español con prompts bien estructurados.
Alternativa: `qwen:1.5b` si `phi3` resulta lento en el hardware objetivo.

**`application.yml`**
```yaml
spring:
  ai:
    ollama:
      base-url: http://ollama:11434
      chat:
        options:
          model: phi3
          temperature: 0.3          # determinista — reduce alucinaciones
          num_predict: 800          # límite de tokens en la respuesta
```

---

## 3. Dominio

### 3.1 Entidad `WeeklyReport`

```
id            : Long
userId        : Long
weekStart     : LocalDate     ← lunes de la semana (unique por user+weekStart)
weekEnd       : LocalDate     ← domingo de la semana
aiContent     : String        ← texto generado por el modelo
generatedAt   : Instant
```

Coincide con la tabla `weekly_reports` del schema v1.2.

### 3.2 Reglas de negocio

- **Mínimo de días registrados:** se requieren al menos **3 DailyEntry con logs**
  para generar el reporte. Si hay menos, se lanza `InsufficientDataException`.
- **Un reporte por semana por usuario:** si ya existe `weekly_reports` para ese
  `userId + weekStart`, el reporte se **sobreescribe** (el usuario puede pedir
  regenerarlo si considera que los datos cambiaron).
- **La semana siempre es lunes→domingo:** `weekStart` se normaliza al lunes
  de la semana del día actual independientemente de lo que envíe el cliente.

### 3.3 Excepciones de dominio

```java
InsufficientDataException    // 422 — menos de 3 días con logs en la semana
ReportGenerationException    // 503 — Ollama no disponible o timeout
```

---

## 4. Trigger — ¿cuándo se genera el reporte?

El reporte tiene **dos triggers**, no uno:

### Trigger A — Automático (Scheduler)
Todos los domingos a las 23:59 mediante `@Scheduled` de Spring Boot.
Itera sobre todos los usuarios activos y genera el reporte de la semana en curso.
Se ejecuta de forma **asíncrona** para no bloquear el hilo del scheduler.

```java
// infrastructure/scheduler/WeeklyReportScheduler.java
@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final GenerateWeeklyReportPort generateReport;
    private final UserRepositoryPort userRepo;

    @Scheduled(cron = "59 23 * * SUN")   // domingos 23:59
    public void generateReportsForAllUsers() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        userRepo.findAllActive().forEach(user ->
            CompletableFuture.runAsync(() ->
                generateReport.execute(new GenerateWeeklyReportCommand(
                    user.getId(), weekStart, triggered_by = "SCHEDULER"
                ))
            )
        );
    }
}
```

### Trigger B — On-demand (HTTP)
El usuario solicita el reporte desde el frontend en cualquier momento.
El endpoint es **síncrono pero con timeout extendido** (30 segundos).
El frontend muestra un skeleton loader mientras espera.

```
POST /api/reports/weekly/generate
```

Si ya existe un reporte para esa semana → lo **sobreescribe** y retorna el nuevo.

---

## 5. Arquitectura Hexagonal

### 5.1 Estructura de paquetes

```
application/
  dto/
    GenerateWeeklyReportCommand.java
    WeeklyReportResult.java
  port/
    in/
      GenerateWeeklyReportPort.java
    out/
      AiAssistantPort.java
      WeeklyReportRepositoryPort.java
  usecase/
    GenerateWeeklyReportUseCase.java

domain/
  model/
    WeeklyReport.java
  exception/
    InsufficientDataException.java
    ReportGenerationException.java

infrastructure/
  ai/
    OllamaAiAdapter.java          ← implementa AiAssistantPort
    PromptBuilder.java            ← construye el prompt estructurado
  persistence/
    entity/
      WeeklyReportJpaEntity.java
    repository/
      WeeklyReportJpaRepository.java
    adapter/
      WeeklyReportRepositoryAdapter.java
  scheduler/
    WeeklyReportScheduler.java

presentation/
  controller/
    WeeklyReportController.java
  response/
    WeeklyReportResponse.java
  mapper/
    WeeklyReportMapper.java
```

### 5.2 Puerto de entrada

```java
// application/port/in/GenerateWeeklyReportPort.java
public interface GenerateWeeklyReportPort {
    WeeklyReportResult execute(GenerateWeeklyReportCommand command);
}
```

### 5.3 Puerto de salida — IA

```java
// application/port/out/AiAssistantPort.java
// El use case no sabe que existe Ollama — solo conoce esta interfaz
public interface AiAssistantPort {
    String generateWeeklyInsight(String structuredPrompt);
}
```

### 5.4 Puerto de salida — Persistencia

```java
// application/port/out/WeeklyReportRepositoryPort.java
public interface WeeklyReportRepositoryPort {
    Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    WeeklyReport save(WeeklyReport report);
}
```

---

## 6. Command y Result

```java
// application/dto/GenerateWeeklyReportCommand.java
public record GenerateWeeklyReportCommand(
    Long      userId,
    LocalDate weekStart,   // se normaliza a lunes en el use case
    String    triggeredBy  // "SCHEDULER" | "USER"
) {}

// application/dto/WeeklyReportResult.java
public record WeeklyReportResult(
    Long      id,
    LocalDate weekStart,
    LocalDate weekEnd,
    String    aiContent,
    Instant   generatedAt
) {}
```

---

## 7. Use Case

```java
// application/usecase/GenerateWeeklyReportUseCase.java
@Service
@RequiredArgsConstructor
public class GenerateWeeklyReportUseCase implements GenerateWeeklyReportPort {

    private final GetWeeklyEntriesPort    weeklyEntriesPort;  // reutiliza UC-07
    private final AiAssistantPort         aiAssistant;
    private final WeeklyReportRepositoryPort reportRepo;
    private final PromptBuilderPort       promptBuilder;

    private static final int MIN_DAYS_REQUIRED = 3;

    @Override
    public WeeklyReportResult execute(GenerateWeeklyReportCommand cmd) {

        // 1. Normalizar weekStart al lunes de esa semana
        LocalDate weekStart = cmd.weekStart().with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = weekStart.plusDays(6);

        // 2. Obtener entradas de la semana (reutiliza UC-07 de feature #4)
        List<DailyEntryWithLogsResult> entries =
            weeklyEntriesPort.execute(cmd.userId(), weekStart);

        // 3. Validar mínimo de días con datos
        if (entries.size() < MIN_DAYS_REQUIRED) {
            throw new InsufficientDataException(entries.size(), MIN_DAYS_REQUIRED);
        }

        // 4. Construir el prompt estructurado (ver sección 8)
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // 5. Llamar al modelo — puede tardar 5-15 segundos
        String aiContent = aiAssistant.generateWeeklyInsight(prompt);

        // 6. Construir entidad de dominio (upsert — sobreescribe si existe)
        WeeklyReport report = reportRepo
            .findByUserIdAndWeekStart(cmd.userId(), weekStart)
            .map(existing -> existing.update(aiContent))
            .orElseGet(() -> WeeklyReport.create(
                cmd.userId(), weekStart, weekEnd, aiContent
            ));

        // 7. Persistir
        WeeklyReport saved = reportRepo.save(report);

        return new WeeklyReportResult(
            saved.getId(), saved.getWeekStart(),
            saved.getWeekEnd(), saved.getAiContent(), saved.getGeneratedAt()
        );
    }
}
```

---

## 8. Prompt — System Prompt y serialización del input

### 8.1 PromptBuilder

El `PromptBuilder` vive en infraestructura porque conoce el formato
específico que espera el modelo. El use case lo ve como un puerto.

```java
// infrastructure/ai/PromptBuilder.java
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
        data.append("Semana del %s al %s:\n\n"
            .formatted(weekStart, weekEnd));

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

    private void serializeStudy(List<StudyLog> logs, StringBuilder sb) {
        if (logs.isEmpty()) return;
        logs.forEach(l -> {
            if (l.isStudied())
                sb.append("Estudio: Sí — %.1fhs de %s\n"
                    .formatted(l.getHours(), l.getSubject()));
            else
                sb.append("Estudio: No — %s\n".formatted(l.getSkipReason()));
        });
    }

    private void serializeExercise(List<ExerciseLog> logs, StringBuilder sb) {
        if (logs.isEmpty()) return;
        logs.forEach(l -> {
            if (l.isExercised())
                sb.append("Ejercicio: Sí — %.1fhs, %s, energía %d%%\n"
                    .formatted(l.getHours(), l.getMuscleGroup(), l.getEnergyLevel()));
            else
                sb.append("Ejercicio: No — %s\n".formatted(l.getSkipReason()));
        });
    }

    private void serializeNutrition(List<NutritionLog> logs, StringBuilder sb) {
        if (logs.isEmpty()) return;
        logs.forEach(l ->
            sb.append("Alimentación: %s%s\n".formatted(
                l.getRating(),
                l.getMetGoal() != null
                    ? (l.getMetGoal() ? " — cumplió objetivo" : " — no cumplió objetivo")
                    : ""
            ))
        );
    }

    private void serializeMood(List<MoodLog> logs, StringBuilder sb) {
        if (logs.isEmpty()) return;
        logs.forEach(l -> {
            sb.append("Ánimo: %s".formatted(l.getMood()));
            if (l.getEventDescription() != null)
                sb.append(" (%s)".formatted(l.getEventDescription()));
            if (Boolean.TRUE.equals(l.isSocialized()))
                sb.append(" — socializó con %s".formatted(l.getSocialWith()));
            sb.append("\n");
        });
    }

    private void serializeSleep(List<SleepLog> logs, StringBuilder sb) {
        if (logs.isEmpty()) return;
        logs.forEach(l -> {
            sb.append("Sueño: %.1fhs (%s)".formatted(l.getHours(), l.getQuality()));
            if (l.isNapped())
                sb.append(" + siesta %.1fhs".formatted(l.getNapHours()));
            sb.append("\n");
        });
    }
}
```

### 8.2 Ejemplo de lo que recibe el modelo

```
Semana del 2026-04-20 al 2026-04-26:

--- 2026-04-21 ---
Estudio: Sí — 2.0hs de Java Hexagonal Architecture
Ejercicio: Sí — 1.0hs, CHEST, energía 80%
Alimentación: GOOD — cumplió objetivo
Ánimo: HAPPY — buen día productivo
Sueño: 7.5hs (GOOD)

--- 2026-04-22 ---
Estudio: No — estaba cansado
Ejercicio: No — dolor muscular
Alimentación: REGULAR
Ánimo: NEUTRAL
Sueño: 6.0hs (REGULAR) + siesta 1.0hs
```

---

## 9. Adaptador Ollama

```java
// infrastructure/ai/OllamaAiAdapter.java
// Único lugar que sabe que existe Ollama — implementa AiAssistantPort
@Component
@RequiredArgsConstructor
public class OllamaAiAdapter implements AiAssistantPort {

    private final ChatClient chatClient;   // inyectado por Spring AI

    @Override
    public String generateWeeklyInsight(String structuredPrompt) {
        try {
            return chatClient.prompt()
                .user(structuredPrompt)
                .call()
                .content();
        } catch (Exception e) {
            throw new ReportGenerationException(
                "Error al conectar con Ollama: " + e.getMessage(), e
            );
        }
    }
}
```

---

## 10. Endpoint HTTP (on-demand)

```
POST /api/reports/weekly/generate
Authorization: Bearer {token}
```

**Request body:** vacío — el `userId` viene del JWT y el `weekStart`
se calcula como el lunes de la semana actual.

**Response 200:**
```json
{
  "success": true,
  "message": "Reporte generado exitosamente",
  "data": {
    "id": 1,
    "weekStart": "2026-04-20",
    "weekEnd":   "2026-04-26",
    "aiContent": "## RESUMEN GENERAL\n...",
    "generatedAt": "2026-04-24T23:59:01Z"
  }
}
```

**Response 422** (datos insuficientes):
```json
{
  "success": false,
  "message": "Se necesitan al menos 3 días registrados. Días encontrados: 2",
  "data": null
}
```

**Response 503** (Ollama no disponible):
```json
{
  "success": false,
  "message": "El servicio de IA no está disponible temporalmente. Intenta más tarde.",
  "data": null
}
```

---

## 11. Manejo de errores

| Excepción | HTTP | Cuándo |
|---|---|---|
| `InsufficientDataException` | 422 | Menos de 3 días con logs en la semana |
| `ReportGenerationException` | 503 | Ollama caído, timeout, error de red |

El `GlobalExceptionHandler` de la feature #4 se extiende con estos dos casos.

---

## 12. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Inferencia lenta (5-15 seg en CPU) | Timeout de 30s en el cliente HTTP del frontend. Skeleton loader con mensaje "La IA está analizando tu semana...". El Scheduler corre async sin bloquear. |
| Alucinaciones del modelo | System prompt restrictivo con reglas explícitas. `temperature: 0.3`. El prompt solo provee datos reales — el modelo no puede inventar campos que no existen. |
| Ollama no arranca | `ReportGenerationException` → 503. El Scheduler loguea el error y continúa con el siguiente usuario sin romper el batch. |
| Modelo no descargado al primer inicio | Script `ollama-entrypoint.sh` hace `pull` automático en el arranque del contenedor. |
| Reporte con formato inesperado | El system prompt especifica títulos exactos de secciones. El frontend renderiza el `aiContent` como Markdown — si el modelo no respeta el formato, igual se muestra legible. |

---

## 13. Criterios de éxito

- [ ] El Scheduler genera reportes automáticamente los domingos a las 23:59
- [ ] El endpoint on-demand genera y retorna el reporte en menos de 30 segundos
- [ ] Con menos de 3 días registrados se retorna 422 con mensaje claro
- [ ] Si ya existe un reporte para la semana, se sobreescribe con el nuevo
- [ ] Ollama caído retorna 503 sin romper el resto de la aplicación
- [ ] El `OllamaAiAdapter` es el único archivo con imports de Spring AI
- [ ] El use case es testeable con un mock de `AiAssistantPort` que retorna un string fijo
- [ ] El reporte generado contiene las 5 secciones definidas en el system prompt