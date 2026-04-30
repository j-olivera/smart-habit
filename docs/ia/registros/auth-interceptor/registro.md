# Auth Interceptor - Registro de Arquitectura

## Objetivo
El `auth.interceptor.ts` funciona como un "middleware" en el frontend para interceptar todas las peticiones HTTP que salen de la aplicación hacia el backend.

## Funciones Clave
1.  **Transporte Seguro (HttpOnly Cookies)**:
    -   Por defecto, las peticiones HTTP (XHR/Fetch) en navegadores no adjuntan las cookies si se dirigen a un dominio/puerto distinto (Cross-Origin).
    -   El interceptor clona cada petición y le inyecta `withCredentials: true`. Esto le dice al navegador: "Por favor, adjunta la cookie del JWT y del Refresh Token en este viaje".
2.  **Renovación Silenciosa (Refresh Token)**:
    -   Si el Access Token (JWT) expiró, el backend devuelve un error `401 Unauthorized`.
    -   El interceptor atrapa ese error antes de que llegue al componente.
    -   Pone las demás peticiones "en pausa" (usando un `BehaviorSubject`).
    -   Llama a `/auth/refresh` silenciosamente.
    -   Si el backend renueva la cookie, el interceptor reanuda las peticiones pausadas. ¡El usuario nunca nota que su sesión estuvo a punto de expirar!
    -   Si el refresh falla (porque el Refresh Token también expiró), ejecuta el `logout()` duro.

## Sinergia con Auth Guard
-   **Auth Guard**: Es el patovica en la puerta de la discoteca (Rutas de UI). Decide si puedes entrar a `/app/dashboard` mirando si tienes la marca en la mano (`localStorage.isLoggedIn`).
-   **Auth Interceptor**: Es el contacto dentro de la fiesta. Si te das cuenta de que tu bebida expiró (Token), él va a la barra silenciosamente, pide una nueva usando el ticket especial (Refresh Token), y te la da sin que te echen de la fiesta. Si no le aceptan el ticket en la barra, él mismo llama a seguridad para que te saquen (`logout`).
