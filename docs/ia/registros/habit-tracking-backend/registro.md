# Registro de Implementación: Hábitos y Seguimiento Diario (Backend)

## 1. Contexto y Alcance
Implementación del motor de hábitos y registros diarios (`DailyEntry`) junto con todos sus logs asociados (Estudio, Ejercicio, Nutrición, Ánimo y Sueño). Este componente es el núcleo de la aplicación y la fuente de datos primaria para la futura Feature de IA.

## 2. Implementaciones Realizadas

### 2.1 Dominio y Aplicación (Cerebro)
- **Patrón Upsert en Logs**: Refactorización de los 5 casos de uso de registro de logs para actuar como *Update or Insert*. El sistema ahora verifica si ya existe un registro para ese hábito en el día y lo actualiza conservando su ID, mejorando la UX y evitando duplicados.
- **Normalización Semanal**: Implementación del `GetWeeklyEntriesUseCase` que fuerza el inicio de semana al lunes anterior a la fecha provista. Esto garantiza bloques de 7 días coherentes para el análisis de la IA.
- **Result Objects**: Creación de `DailyEntryWithLogsResult` y `WeeklyEntriesReportDto` para desacoplar la respuesta de los Use Cases de las entidades de dominio puro.

### 2.2 Infraestructura y Persistencia (Músculos)
- **Entidades JPA**: Mapeo completo de `HabitEntity`, `DailyEntryEntity` y las 5 entidades de log (`StudyLogEntity`, `ExerciseLogEntity`, etc.).
- **Mappers Quirúrgicos**: Implementación de mappers independientes para desacoplar las capas, manejando conversiones de tipos (ej: `Instant` <-> `LocalDateTime`) y Enums.
- **Consultas Optimizadas**: El `DailyEntryRepositoryAdapter` utiliza cargas agrupadas (`findAllByEntryIdIn`) para hidratar los reportes semanales, evitando el problema de N+1 consultas.

### 2.3 Controladores y Seguridad (Piel)
- **`HabitController`**: Gestión de definiciones de hábitos.
- **`HabitLogController`**: Endpoint unificado para los 5 tipos de logs usando el patrón Upsert.
- **`DailyEntryController`**: Endpoints de consulta diaria y semanal.
- **Seguridad Garantizada**: Todos los endpoints extraen el `userId` directamente del contexto de seguridad (`@AuthenticationPrincipal`), previniendo ataques de tipo IDOR (Insecure Direct Object Reference).

## 3. Calidad y Validación (Tests)
Se implementó una suite de 10 tests unitarios e integrales (WebMvcTest) cubriendo:
- Lógica de normalización de fechas en reportes semanales.
- Integridad de IDs en procesos de actualización (Upsert).
- Mapeo correcto de entidades de persistencia a resultados de aplicación.
- Seguridad en controladores y extracción de usuario desde JWT.

## 4. Archivos Clave
- `DailyEntryRepositoryAdapter.java`: Orquestador de la hidratación de datos.
- `GetWeeklyEntriesService.java`: Lógica de normalización de semanas.
- `HabitLogController.java`: Punto de entrada para registros diarios.
- `LogUseCasesTest.java`: Validación de la lógica de negocio de los logs.

## 5. Test cubiertos

1. HabitQueryUseCasesTest: Validamos que la obtención del día funcione y, lo más
   importante, que el reporte semanal normalice las fechas correctamente (forzando
   Lunes a Domingo) para que la IA no se maree.
2. LogUseCasesTest: Verificamos la lógica de Upsert. Probamos que si el log no
   existe se crea, y si ya existe se actualiza conservando el mismo ID (crucial
   para no romper la integridad en la DB).
3. DailyEntryRepositoryAdapterTest: Testeamos el mapeo complejo y optimizado.
   Aseguramos que el adaptador sea capaz de agrupar los diferentes logs dentro de
   sus respectivos días al armar el reporte semanal.
4. HabitControllerTest y DailyEntryControllerTest: Validamos que los endpoints
   estén bien mapeados y que la seguridad de Spring Security esté haciendo su
   laburo (extrayendo el usuario del contexto de autenticación).

## 6. Próximos Pasos
- Implementación de la **Feature 5**: Integración con Ollama para generar el `WeeklyReport` analizando la data recolectada por estos componentes.
- Configuración del Scheduler para generación automática de reportes los domingos.
