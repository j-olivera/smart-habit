# Idea - Smart Habit 

## Objetivo

- Crear una aplicacion web que permita cargar los habitos semanales de un usuario, para al finalizar la semana realizar un reporte orquestado por IA, que permita al usuario conocer sus habitos, en que aspecto mejorar, marcar riesgos, hacer recomendaciones, etc. 

## Arquitectura

### Frontend

- El frontend estara programado en Angular, utilizando TypeScript, HTML, TailwindCSS, y TypeScript. Conectandose atravez de una API REST con el backend.

### Backend

- El backend estara programado con Java 21, utilizando Spring Boot como framework, y PostgreSQL como base de datos. Se conectara con el frontend atravez de una API REST.

### Lineamientos

- El proyecto utilizara el patron S.O.L.I.D, y Clean Architecture.
- Se utilizara Docker para el despliegue de la aplicacion.
- Seguira el patron TDD.
- Se utilizara Git para el control de versiones.

### Configuración y Setup

1. Clonar el repositorio.
2. Crear un archivo `.env` en la raíz del proyecto basado en `.env.example`:
```env
# Base de Datos
POSTGRES_DB=smarthabit
POSTGRES_USER=postgres
POSTGRES_PASSWORD=strong_password

# Seguridad
JWT_SECRET=super_secret_jwt_key_for_development

# Inteligencia Artificial
OLLAMA_MODEL=llama3
```
3. Levantar la infraestructura completa con Docker: `docker compose up --build`

### Dominio-Habito

- Los Habitos seran los siguientes:
    - Estudio -> ¿Si,no?(boolean)
        - Si es si -> ¿Cuantas horas?(int), ¿Que estudio?(String)
        - Si es no -> ¿Por que no?(String)
    - Ejercicio -> ¿Si,no?(boolean)
        - Si es si -> ¿Cuantas horas?(int), ¿Que grupos musculares?((ENUM)Pecho, Espalda, Piernas, Brazos, Abdomen, Cardio), ¿Como te sentiste de energia en el entrenamiento?(int 1-25, 25-50, 50-75, 75-100(%))
        - Si es no -> ¿Por que no?(String)
    - Alimentacion -> ¿Como consideras que fue tu alimentacion hoy?((ENUM)  Mala, Regular, Buena, Excelente)
        - Observaciones -> ¿Si,no?(boolean)
            - Si es si -> ¿Consideras que cumpliste con una buena alimentacion segun tu objetivo?(boolean)
            - Si es no -> *pasa a la siguiente casilla
    - Animo/Social -> ¿Como te sentiste hoy?((ENUM)  Mal, Decaido, Neutro, Feliz, Euforico)
        - Observaciones -> ¿Si,no?(boolean)
            - Si es si -> ¿Paso algo que hizo que tu dia este de esta manera?(String)
            - Si es no -> *pasa a la siguiente casilla
        - Socializaste -> ¿Si,no?(boolean)
            - Si es si -> ¿Con quien socializaste?(String)
            - Si es no -> *pasa a la siguiente casilla
    - Sueño -> ¿Cuantas horas dormiste anoche?(float), ¿Que tan bien consideras que dormiste?((ENUM) Mal, Regular, Bien, Excelente)
        - ¿Dormiste siesta? (boolean)
            - Si es si -> ¿Cuantas horas dormiste siesta?(float), ¿Crees que era necesario?(boolean)
            - Si es no -> *pasa a la siguiente casilla
    
        
### Motor de la IA

- El backend integrara un motor de IA que permita analizar los habitos del usuario y generar un reporte personalizado. 
- Probaremos con Spring AI y Ollama, si no funciona, probaremos con otra alternativa.


### Generador de reporte

- El reporte se genera todos los domingos a las 23:59, mediante la funcion Scheduler de Spring Boot.
- El reporte se enviara al correo electronico del usuario.

### Identidad y autenticación

- Utilizaremos un registro simple, nombre, correo y contraseña. Los datos se guardaran en la base de datos PostgreSQL. Mas detalles se veran en el backend 
- El login se hara mediante correo y contraseña, y se generara un token JWT para la autenticación.
- En el frontend tendremos un authservice el cual se conectara con el backend para el registro y login.

### Interfaz de usuario

- Se deja a criterio de la IA el diseño de la interfaz de usuario, pero debe seguir los lineamientos de S.O.L.I.D, y Clean Architecture.


## Orden de features
0. [feature] database-setup
0. [feature] docker-setup
1. [feature] user-register-backend
2. [feature] user-login-backend
3. [feature] user-register-habit-daily-backend
4. [feature] ia-report-generator-backend
5. [feature] user-register-frontend
6. [feature] user-login-frontend
7. [feature] user-register-habit-daily-frontend
8. [feature] ia-report-generator-frontend