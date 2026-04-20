# Convenciones del código

## Lineamientos de trabajo

- Leer los archivos antes de generar codigo
- Mantener la consistencia de naming: camelCase en Java y kebab-case en Angular
- Toda entidad nueva en java debe tener su DTO y mapper correspondiente
- Todas las respuestas de API tendran el formato ApiResponse<T>
- Idioma: Codigo en ingles y comentarios/docs en español

### Generales

- Testing (TDD) -> Ninguna funcionalidad se considera terminada si no tiene sus tests correspondientes pasando R -> G -> R 
- Commits -> Seguiremos la convencion de Conventional Commits
- Nuevas features -> Toda nueva feature primero debe pasar por una fase de diseño (Spec/Design) y ser aprobada por el usuario
- Persistencia de contexto -> Todas las specs y diseños se guardaran en Engram

### Java (Backend)

- DTOs -> Deben ser inmutables y usar record
- Manejo de excepciones -> Centralizado en un @RestControllerAdvice y GloblalHandlerException
- LOGs -> SLF4J + Logback
- Scheduler Job -> @Scheduled para el resumen programado
- Flujo de datos -> Controller -> Service -> Repository -> Database

### Angular (Frontend)

- Standalone components -> No usar ngmodules
- Signals -> Usar signals para el manejo de estado
- HttpClient -> Usar HttpClient para las peticiones HTTP
- Lazy loading en todas las rutas de las features
- Testing Unitario -> Usar Vitest (runner por defecto de Angular 21, más rápido que Karma/Jasmine)
- Testing E2E -> Usar Playwright (para testear los flujos críticos como el registro de hábitos)

### Docker (Despliegue / Entorno)

- Todo el proyecto debe poder levantarse con `docker compose up --build`
- Mantener arquitectura multi-servicio desacoplada
- Nunca hardcodear credenciales en Dockerfile (Usar variables por .env)
- Usar hostname del servicio (ej. postgres:5432, ollama:11434)
- PostgreSQL exige persistencia por volume
- Todo servicio crítico debe tener healthcheck (backend, postgres, ollama)
