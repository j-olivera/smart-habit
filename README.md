# Smart Habit Tracker

**Smart Habit Tracker** es una aplicación integral diseñada para ayudar a los usuarios a registrar, monitorear y mejorar sus hábitos diarios mediante el uso de Inteligencia Artificial. A diferencia de los rastreadores convencionales, este sistema no solo registra si un hábito se cumplió o no, sino que analiza la calidad del sueño, la nutrición, el estado de ánimo, el estudio y el ejercicio para generar **reportes semanales con insights personalizados utilizando IA (Ollama)**.

---

## Objetivo del Proyecto

El objetivo principal es proporcionar una herramienta que entienda el contexto del usuario. Si un usuario no hizo ejercicio porque durmió mal y estuvo estresado, el sistema (a través de la IA) es capaz de correlacionar estos eventos y ofrecer recomendaciones accionables y empáticas para la semana siguiente, promoviendo un bienestar holístico y sostenible.

---

## Arquitectura y Tecnologías

El proyecto está dividido en dos aplicaciones principales y sigue los principios de la **Clean Architecture** (Arquitectura Hexagonal) en el backend.

### Backend (Java / Spring Boot)
- **Framework**: Spring Boot 3.x
- **Lenguaje**: Java 21
- **Arquitectura**: Hexagonal (Domain, Application, Infrastructure)
- **Seguridad**: Spring Security con JWT (JSON Web Tokens) vía **HttpOnly Cookies** y Refresh Tokens.
- **Base de Datos**: PostgreSQL
- **Migraciones**: Flyway
- **Inteligencia Artificial**: Integración con [Ollama](https://ollama.com/) (modelo `phi3`) usando Spring AI para la generación de reportes semanales.

### Frontend (Angular)
- **Framework**: Angular 18 (Standalone Components)
- **Lenguaje**: TypeScript
- **Estilos**: Tailwind CSS
- **Estado**: Angular Signals para reactividad y manejo de estado ligero.
- **Seguridad**: Rutas protegidas mediante Guards Funcionales (`canActivateFn`).

---

## Despliegue Rápido con Docker

El proyecto está dockerizado para facilitar su levantamiento en cualquier entorno. El archivo `docker-compose.yml` orquesta la base de datos, el backend, el frontend y el motor de IA (Ollama).

### Requisitos Previos
- Docker y Docker Compose instalados.
- Puertos disponibles: `5432` (Postgres), `8080` (Backend), `4200` (Frontend), `11434` (Ollama).

### Pasos para levantar el entorno

1. **Clonar el repositorio y configurar variables:**
   Crea un archivo `.env` en la raíz del proyecto basándote en el `.env.example` (si existe) o asegúrate de que las variables por defecto en el `docker-compose.yml` sean correctas.

2. **Levantar los contenedores:**
   Ejecuta el siguiente comando en la raíz del proyecto:
   ```bash
   docker-compose up -d --build
   ```

3. **Descargar el modelo de Inteligencia Artificial (IMPORTANTE):**
   El contenedor de Ollama se levanta vacío. Para que la IA funcione y genere los reportes semanales, debes descargar el modelo `phi3` dentro del contenedor. Ejecuta:
   ```bash
   docker exec -it smart-ollama ollama run phi3
   ```
   *(Este proceso puede tardar unos minutos dependiendo de tu conexión a internet).*

4. **Acceder a la aplicación:**
   - **Frontend**: http://localhost:4200
   - **Backend API**: http://localhost:8080
   - **Ollama API**: http://localhost:11434

---

## Seguridad y Autenticación

El sistema implementa un robusto flujo de seguridad:
- **HttpOnly Cookies**: Los tokens JWT nunca están accesibles vía JavaScript, mitigando ataques XSS.
- **Refresh Tokens**: Permiten mantener la sesión del usuario viva de forma transparente sin requerir re-autenticación constante.
- **Auth Guard**: El frontend protege las rutas privadas (`/app/*`) verificando el estado de la sesión antes de renderizar.

---

## Estructura del Proyecto

```text
smart-habit-project/
├── smart-backend/        # API RESTful, lógica de negocio y conexión DB/IA
├── smart-habit-frontend/ # Aplicación cliente, UI y consumo de API
├── docs/                 # Documentación técnica y arquitectura de agentes
├── docker-compose.yml    # Orquestador de contenedores
└── README.md             # Este archivo
```