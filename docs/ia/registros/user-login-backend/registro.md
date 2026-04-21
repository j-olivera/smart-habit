# Registro de Feature: user-login-backend

**Fecha de finalización:** 2026-04-21  
**Arquitectura Aplicada:** Clean Architecture / Hexagonal Architecture (Domain, Application, Infrastructure)

## Resumen Ejecutivo
Se implementó el flujo completo de autenticación JWT con refresh tokens para SmartHabitApp. El sistema acepta credenciales (email/password) en `POST /api/auth/login`, retorna un Access Token JWT (15 min) en el body y un Refresh Token (UUID) en cookie HttpOnly/Secure para `/api/auth/refresh` y logout.

---

## Tareas Completadas (SDD Phases)

### Phase 1: Database & Dependencies (Infrastructure)
- **1.1 JJWT Dependencies:** Agregadas dependencias `jjwt-api`, `jjwt-impl`, `jjwt-jackson` y `jackson-databind` en `pom.xml`.
- **1.2 Flyway Migration:** Creado `V2__create_refresh_tokens_table.sql` con columnas: id, user_id, token_hash, expires_at, revoked, created_at.
- **1.3 RefreshTokenEntity:** Entidad JPA en `infrastructure/model` con ManyToOne lazy hacia UserEntity.

### Phase 2: Domain & Persistence Adapters
- **2.1 RefreshToken (Domain):** POJO puro inmutable en `domain/RefreshToken.java` (id, userId, tokenHash, expiresAt, revoked).
- **2.2 RefreshTokenRepositoryPort:** Interfaz Out-Port en `application/port/out`.
- **2.3 JpaRefreshTokenRepository:** Repositorio Spring Data en `infrastructure/repository`.
- **2.4 RefreshTokenRepositoryAdapter:** Adaptador implementando el Port con MapStruct para mapping entity↔domain.
- **2.4 Tests:** Unit test y Integration test (DataJpaTest) desarrollados y corregidos (bug fix: mapper no estaba en @Import).

### Phase 3: Token Providers (Infra to App boundaries)
- **3.1 JwtProviderPort:** Interfaz Out-Port en `application/port/out` con métodos `generateToken(User)`, `validateToken(String, User)`, `extractUserEmail(String)`.
- **3.2 JwtAdapter:** Implementación concreta en `infrastructure/security/JwtAdapter` usando JJWT. Inyecta `JWT_SECRET` via application.yml. Maneja prefix "Bearer ". Tests mockeando secret.

### Phase 4: Application Layer (Use Cases)
- **4.1 DTOs:** `LoginRequestDto` y `AuthResultDto` (accessToken, refreshToken).
- **4.2 LoginUserService:** Implementa LoginUserUseCase. Fetch usuario por email, valida password, genera JWT, crea RefreshToken UUID, persiste y retorna AuthResultDto.
- **4.3 RefreshSessionService:** Implementa RefreshSessionUseCase. Valida token existente y no expirado/revocado, marca旧 revoked, genera nuevo token, rota JWT. retorna AuthResultDto.
- **4.4 LogoutUserService:** Implementa LogoutUserUseCase. Recibe refresh token, marca como revoked en DB.
- **4.5 Unit Tests:** Tests para los 3 servicios con Mockito en `AuthUseCasesTest.java`.

### Phase 5: Infrastructure Layer (API & Security)
- **5.1 DTOs Extra:** `TokenResponseDto` (accessToken) y `MessageResponseDto` (message).
- **5.2 AuthController Endpoints:**
  - `POST /api/auth/login` → retorna TokenResponseDto + Set-Cookie (refreshToken HttpOnly, Secure, 7 días)
  - `POST /api/auth/refresh` → recibe cookie refreshToken, retorna nuevos tokens
  - `POST /api/auth/logout` → revoca token, clear cookie con Max-Age=0
- **5.3 JwtAuthenticationFilter:** Filter en `infrastructure/security` extiende `OncePerRequestFilter`. Valida header "Authorization: Bearer <token>", extrae email, setea `UsernamePasswordAuthenticationToken` en SecurityContext.
- **5.4 SecurityConfig:** Refactorizado para agregar el filtro antes de UsernamePasswordAuthenticationFilter. Sesión STATELESS.
- **5.5 WebMvcTest:** `AuthControllerTest.java` con 8 tests cubriendo todos los endpoints y casos de éxito/error.

---

## Decisiones Críticas / Metodología
1. ** Refresh Token Strategy:** Se opted por estrategia stateful (token en DB) para permitir revocación estricta. UUID como token hash (no JWT) para diferenciarlo del access token.
2. **Cookie Security:** Refresh token en HttpOnly + Secure + SameSite=Strict. Path específico a `/api/auth/refresh`.
3. **Stateless Session:** JWT sin estado en servidor, pero refresh token con estado en DB para permitir logout forzado de sesiones.

## Estado Final
- **Tests Completados:** 17 / 17 pasando correctamente (`mvnw test`).
- **Commiteable:** La feature user-login-backend se declara COMPLETA y apta para merge.