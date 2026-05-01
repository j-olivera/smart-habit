# Registro de Tarea: AI Weekly Report Historial

## Información General
- **Fecha**: 2026-05-01
- **Tarea**: Implementación de historial y visualización de reportes semanales de IA.
- **Backend**: Java / Spring Boot (Arquitectura Hexagonal)
- **Frontend**: Angular (Signals, TailwindCSS)

## Contexto y Objetivo
El sistema ya contaba con la generación automática de reportes semanales mediante IA (Ollama), pero carecía de una interfaz para que el usuario pudiera verlos. Se requería:
1. Poder ver el reporte más reciente.
2. Acceder a un historial de reportes pasados.
3. Solicitar una generación manual si se cumplen los requisitos mínimos de datos.

## Implementación Técnica

### Backend (smart-backend)
Se extendió la infraestructura de reportes siguiendo la arquitectura hexagonal:
- **Puertos**: Se agregaron `GetWeeklyReportsPort` y `GetWeeklyReportByIdPort`.
- **Casos de Uso**:
    - `GetWeeklyReportsUseCase`: Retorna un listado liviano (`WeeklyReportSummary`) de los reportes del usuario.
    - `GetWeeklyReportByIdUseCase`: Retorna el detalle completo de un reporte específico.
- **Controlador**: Se añadieron los endpoints `GET /api/reports` y `GET /api/reports/{id}` en `WeeklyReportController`.
- **Repositorio**: Se implementó `findAllByUserIdOrderByWeekStartDesc` en el adaptador del repositorio.

### Frontend (smart-habit-frontend)
Se creó una nueva sección dedicada a los reportes:
- **Servicio**: `WeeklyReportService` para la comunicación con la API.
- **Componente**: `ReportsComponent` con:
    - **Layout**: Diseño de dos columnas (Historial / Contenido).
    - **Markdown**: Uso de la librería `marked` con `DOMPurify` para renderizar de forma segura el contenido de la IA.
    - **Reactividad**: Uso intensivo de Angular Signals para manejar estados de carga y selección.
- **Rutas**: Configuración de `/app/reports` en el Dashboard Layout.

## Verificación y Pruebas
- **Pruebas de Integración (Backend)**: Se creó `WeeklyReportControllerTest` para validar los nuevos endpoints, incluyendo el manejo de seguridad y casos de "Not Found".
- **Pruebas Manuales**: Verificación del flujo completo: navegación -> listado -> detalle -> generación manual.

## Aprendizajes y Notas
- **Mock de Seguridad**: En los tests de Spring Boot con `@AuthenticationPrincipal`, es necesario inyectar manualmente el objeto `User` de dominio en el `SecurityContext` para evitar `NullPointerException`.
- **Markdown**: El estilo del reporte se manejó con selectores `::ng-deep` en el CSS del componente para aplicar formato al HTML generado dinámicamente por `marked`.

## Artifacts SDD (Engram)
- Proposal: #106
- Spec: #108
- Design: #109
- Tasks: #110
- Archive: #117
