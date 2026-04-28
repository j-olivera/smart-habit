# Implementación de Servicio Auth completada 🚀

Hemos completado exitosamente la implementación del servicio transversal de autenticación en nuestra capa frontend Angular siguiendo las rigurosas convenciones de nuestro querido equipo de arquitectura.

## Cambios realizados

### Modelado Riguroso
- ✅ Mapeamos `RegisterRequest`, `LoginRequest`, `UserResponse`, `TokenResponse`, y `MessageResponse` tal cual como salen del `AuthController` de Spring Boot.

### Environments
- ✅ Se configuró el enviroment para redirigir `apiUrl` a `http://localhost:8080/api` permitiendo así la conexión fluida hacia el sistema bajo Docker Compose local.

### Servicio Auth
- ✅ Creado el servicio mediante la directiva Standalone `@Injectable({providedIn: 'root'})`.
- ✅ Endpoints REST consumidos usando `HttpClient` de Angular.
- ✅ Implementación de señal de estado (Signal): `readonly currentUser = signal<UserResponse | null>(null);` para el manejo de sesiones en vivo en el DOM.
- ✅ Todos los métodos de API críticos llevan la bandera obligatoria `withCredentials: true` con el fin de interceptar la Secure HTTPOnly Cookie para el refresh token.

## Comprobación TDD

> [!TIP]
> Los tests corrieron exitosamente usando el nuevo sistema en Angular 21 (Vitest puro bajo el capo de Webpack/Esbuild) levantado mediante `ng test`. **(5 Pasan de 5 Fallados previamente).**

```bash
 ✓ |smart-habit-frontend| src/app/services/auth/auth.service.spec.ts (5 tests) 35ms
   ✓ AuthService (5)
     ✓ should be created 14ms
     ✓ should send a POST request on register 11ms
     ✓ should send a POST request with credentials on login 3ms
     ✓ should send a POST request with credentials on refresh 3ms
     ✓ should send a POST request with credentials on logout 3ms
```
