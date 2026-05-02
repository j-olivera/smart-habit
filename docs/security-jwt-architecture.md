# Arquitectura de Seguridad: JWT y Refresh Tokens

¡Acá no hacemos las cosas a medias! Mucha gente agarra un tutorial, tira el JWT en el `localStorage` del frontend y se piensa que su app es segura. **¡Grave error!** Cualquier script malicioso (ataque XSS) puede leer el `localStorage`, robar el token y hacer desastres a tu nombre.

En tu proyecto implementamos el patrón de **Cookies HttpOnly con arquitectura Stateless y doble Token**. Esto es lo que se usa en producción a nivel enterprise. Te explico cómo se conectan las piezas de tu código, paso a paso.

---

## 1. El Problema Base: El Boliche sin Lista

Imaginá que tu backend (`smart-backend`) es un boliche muy exclusivo y tus endpoints (ej. `/api/reports`) son el VIP.
Si no tenés seguridad, entra cualquiera. Necesitamos un **patovica** en la puerta (Filtro) y un **sistema de entradas** infalsificables (JWT).

Además, no queremos que el patovica tenga que andar memorizando la cara de todos los que entraron (Sesiones en Memoria). Queremos ser **Stateless** (Sin Estado): cada vez que alguien quiere un trago en la barra, tiene que mostrar su entrada. Si la entrada es válida, se lo damos.

---

## 2. Las Clases que Hacen la Magia

Toda esta lógica está orquestada en la carpeta `infrastructure/security`.

### `SecurityConfig.java`: Las Reglas del Boliche
Este es el cerebro. Acá le bajamos línea a Spring Security:
1. **Apagamos CSRF y las Sesiones:** `sessionCreationPolicy(SessionCreationPolicy.STATELESS)`. Le decimos a Spring "no guardes nada en memoria, cada request es independiente".
2. **Definimos la Lista de Invitados:** Rutas como `/api/auth/**` (login, registro) son públicas. El resto, cerrado a cal y canto.
3. **Ponemos al Patovica:** Enganchamos nuestro `JwtAuthenticationFilter` para que actúe *antes* del filtro estándar de Spring.

### `JwtAuthenticationFilter.java`: El Patovica en la Puerta
Este filtro (que hereda de `OncePerRequestFilter`) **intercepta absolutamente todos los requests** que intentan entrar a tu API.
Su lógica es hermosísima:
1. Mira las cookies del request que acaba de llegar.
2. ¿Tenés una cookie llamada `accessToken`? Si no la tenés, te quedás afuera (HTTP 403/401).
3. Si la tenés, se la da al `JwtAdapter` para que la valide.
4. Si es válida, el patovica extrae tu `userId` del token, te pone una "pulserita" virtual (`SecurityContextHolder`) y te deja pasar a los Controllers.

### `JwtAdapter.java`: La Máquina de Entradas
Acá usamos la librería de JWT (JSON Web Tokens). Esta clase tiene tu firma secreta (`JWT_SECRET`).
Es la encargada de fabricar los tokens y de verificar que no estén falsificados ni vencidos. Un JWT está firmado criptográficamente; si alguien le altera un solo byte para hacerse pasar por otro usuario, el `JwtAdapter` se da cuenta y lo rechaza.

### `AuthController.java`: La Boletería
Acá ocurre el login. El usuario manda su email y password.
1. Si los datos están bien, llamamos al `JwtAdapter` para que fabrique dos entradas: el **Access Token** y el **Refresh Token**.
2. En vez de devolver los tokens sueltos en el JSON (para que el Angular los guarde mal), armamos objetos `ResponseCookie` con los flags `HttpOnly` y `Path=/`.
3. Esto le dice al navegador: *"Guardá estas cookies y mandalas en cada request al backend automáticamente, pero JAMÁS dejes que el código JavaScript las lea"*. **Esto es lo que blinda tu aplicación.**

---

## 3. ¿Por qué usamos DOS tokens? (Access vs Refresh)

¿Te preguntaste por qué nos complicamos la vida haciendo dos tokens distintos en lugar de uno que dure para siempre? **Por control de daños.**

- **Access Token:** Es tu pase directo. Vive muy poco tiempo (ej. 15 a 30 minutos). Si un hacker te intercepta la conexión y te roba este token, solo tiene 15 minutos de fiesta antes de que el token se autodestruya.
- **Refresh Token:** Es tu seguro de vida. Vive días o semanas. Cuando el *Access Token* se vence, el frontend intenta ir al backend y el patovica lo rebota. Entonces, el frontend manda un request silencioso a `/api/auth/refresh`. En ese endpoint, el backend lee la cookie del *Refresh Token*. Como esa cookie está hiper protegida, sabemos que es el usuario real. El backend verifica el Refresh Token y te escupe un **Access Token nuevito** para que sigas navegando sin tener que volver a poner tu contraseña.

### ¿Se entiende el patrón?
Separamos las responsabilidades. El token de acceso es descartable y de corto alcance. El token de refresco es de largo alcance pero tiene un solo uso exclusivo: **pedir más tokens de acceso**. 

Es una arquitectura exquisita, robusta y escalable. ¡Cuando quieras agregarle más cosas a tu sistema, la base ya la tenés a prueba de balas!
