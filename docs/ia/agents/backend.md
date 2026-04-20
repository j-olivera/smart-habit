# AGENTS.md — Agente Backend 
## Identidad
Sos el agente especialista en Java 21 + Spring Boot 3 del proyecto SmartHabitApp. Tu dominio es exclusivamente el directorio backend/. 
## Endpoints a implementar 
### Auth 
POST /api/auth/register → registro del usuario 
POST /api/auth/login → retorna JWT + refresh token 
POST /api/auth/refresh → renueva token 
POST /api/auth/logout → invalida refresh token 
### User 
GET /api/user/me → perfil del paciente autenticado 
PUT /api/user/me → actualizar perfil propio 
GET /api/user/{id} → solo ROLE_ADMIN 

### Habits
POST /api/habits/daily → registro de hábitos de un día específico
GET  /api/habits/daily/{date} → hábitos cargados de un día
GET  /api/habits/weekly → hábitos de la semana actual
GET  /api/habits/summary → resumen semanal generado por IA


## Estructura de packages
domain/ core de la aplicacion, entidades puras
    exceptions/ excepciones custom
application/ dtos(request,response)casos de uso, port in para las interfaces y out para los repositorios, mapper de entidad a dto
infrastructure/
    controller/ solo recibe request, delega al service, retorna DTO 
    adapter/ implementa el puerto de salida 
    repository/ Spring Data JPA, queries JPQL cuando haga falta 
    model/ entidades JPA (nunca salen del service) 
    mapper/ mappers para jpa -> entidad y viceversa 
    security/ JwtFilter, SecurityConfig 
    exception/ GlobalExceptionHandler
## Seguridad obligatoria
- Todos los endpoints protegidos con @PreAuthorize o SecurityConfig 
- /api/auth/** es el único path público 
- Passwords hasheadas con BCrypt (strength 12) 
- JWT: access token expiración 15 min (en memoria o localStorage del front)
- Refresh token: 7 días, almacenado en httpOnly cookie (NUNCA en localStorage por riesgo XSS)
## RESTRICCIONES
- NUNCA retornar la entidad User directamente (siempre UserResponseDto) 
- NUNCA incluir password en ningún DTO de respuesta 
- NUNCA usar FetchType.EAGER en relaciones (riesgo de N+1) 
- NUNCA hacer lógica de negocio en el controller