# Registro de Feature: user-register-backend

**Fecha de finalización:** 2026-04-20  
**Arquitectura Aplicada:** Clean Architecture / Hexagonal Architecture (Domain, Application, Infrastructure)

## Resumen Ejecutivo
Se implementó de principio a fin el flujo de registro de usuarios del backend cumpliendo con las bases técnicas y de arquitectura limpias, desvinculando de raíz cualquier dependencia del framework Spring sobre el núcleo de dominio.

---

## Tareas Completadas (SDD Phases)

### Fase 1: Configuración, DB y Modelo (Infrastructure & Domain)
- **Migración y Estructuras:** Creación del script `V7__add_name_to_users_table.sql` (Flyway) asegurando persistencia real.  
- **Dependencias:** Instalación y configuración de MapStruct, spring-boot-validation y H2 puro test profile en `pom.xml`.
- **Core Domain:** Se implementó el POJO puro inmutable `User.java` sin decoradores restrictivos (`@Entity` prohibido en core).
- **Clean Persistence:** 
  - Interfaces Out-Port (`UserRepositoryPort`).
  - Entidad JPA (`UserEntity`).
  - Adaptador Saliente (`UserRepositoryAdapter`).
  - Testing real R-G-R con JUnit y H2 (`UserRepositoryAdapterTest`).

### Fase 2: Application Layer (Use Cases, DTO & Rules)
- **Contratos (Records):**  `RegisterRequestDto` con validaciones robustas y `UserResponseDto`.
- **Excepciones Propias:** `EmailAlreadyExistsException` creada internamente en dominio.
- **Puertos Extras:** In-Port `RegisterUserUseCase` y el Out-Port abstracto para seguridad `PasswordEncoderPort`.
- **Servicio Principal:** Implementación `RegisterUserService` para orquestar la encriptación y persistencias sin conocer cómo funciona Spring.
- **Unit Testing de Capa Aplicación:** `RegisterUserServiceTest` desarrollado en Mockito simulando las clases de infraestructura (Base de Datos y BCrypt).

### Fase 3: Web y Security (Infrastructure)
- **Web Security:** Setup de seguridad `SecurityConfig.java` inhabilitando el CORS/CSRF localmente y permitiendo el acceso público al endpoint `/api/auth/**`.
- **Framework de Manejo de Excepciones:** Clase `@RestControllerAdvice` (`GlobalExceptionHandler`) mapeando los `@Valid` (HTTP 400 Bad Request) y la colisión de correos (HTTP 409 Conflict) devolviendo salidas predecibles para el cliente de Angular.
- **Implementación REST:** `AuthController` mapeado bajo HTTP POST. Delegado completo al In-Port y retornado HTTP 201 Created.
- **Testing MVC:** Implementación superada exitosamente en `AuthControllerTest` inyectando MockMvc y `@WebMvcTest`. 

---

## Decisiones Críticas / Metodología
1. **Pivote Arquitectónico a Medio Camino:** Al comenzar con una arquitectura en 3 capas estándar (Controller, Service, Repository), interceptamos el vicio y refactorizamos firmemente hacia **Clean Architecture**. Se borraron archivos previos, se limpiaron directorios y se generaron los clásicos adaptadores y puertos.
2. **Abstracción del Encriptor de Spring:** En lugar de acoplar `RegisterUserService` con el encoder de Spring, aplicamos un puerto `PasswordEncoderPort` con una clase adaptador que utiliza el viejo confiable `BCryptPasswordEncoder`.

## Estado Final
- **Tests Completados:** 7 / 7 pasando correctamente en todos los ambientes (`mvnw clean test`).
- **Commiteable:** La etapa para el registro de usuario se declara COMPLETA y apta para merge.
