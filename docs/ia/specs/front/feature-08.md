# Feature 08 - User Register Habit Daily Frontend

## 01 - Description

Como usuario logueado en la aplicación, quiero poder acceder a un Dashboard interactivo con una navegación lateral (Sidebar). En este Dashboard, quiero visualizar los 5 hábitos fijos proporcionados por el sistema y mis hábitos personales. Quiero poder registrar un hábito interactuando con su tarjeta (abriendo un modal). Los hábitos que no he registrado aún deben aparecer en estado "Pendiente" y pasar a "Completado" inmediatamente tras registrarlos.

## 02 - Requerimientos Técnicos

1. **Routing & Layout**:
   - Todo el contenido autenticado debe estar bajo una ruta padre protegida (ej. `/app`) que cargue sus hijos usando Lazy Loading.
   - Implementar `DashboardLayoutComponent` para contener el `<app-sidebar>` y el `<router-outlet>`.

2. **State Management**:
   - Utilizar **Angular Signals** de forma exclusiva para el manejo del estado de los hábitos (`HabitStateService`). No utilizar RxJS BehaviorSubjects para la reactividad de UI local.
   
3. **Sidebar**:
   - Fijo a la izquierda (o como drawer en móviles).
   - Estética Glassmorphism.
   - Enlaces a: Dashboard, Reporte Semanal, Cerrar Sesión.

4. **Dashboard**:
   - Vista de grilla (Bento Grid) moderna con Tailwind 4.
   - Cruce de datos: Los metadatos de los hábitos fijos (íconos, títulos) deben vivir en una constante del front. Se deben renderizar los 5 fijos siempre, en estado "Pendiente" si no hay datos del backend.

## 03 - Scenarios

### Scenario 1: Navegación Segura al Dashboard
- **Given** que el usuario ha iniciado sesión correctamente
- **When** es redirigido a `/app/dashboard`
- **Then** debe ver el Sidebar cargado a la izquierda y el Dashboard principal.

### Scenario 2: Estado Inicial de Hábitos
- **Given** que es un nuevo día y el usuario no ha registrado hábitos
- **When** entra al Dashboard
- **Then** debe ver las tarjetas de "Estudio", "Ejercicio", "Nutrición", "Ánimo" y "Sueño" con un estado visual de "Pendiente" y opacidad/estilo diferenciado.

### Scenario 3: Abrir Modal de Hábito
- **Given** que el usuario está en el Dashboard
- **When** hace clic en la tarjeta del hábito "Estudio"
- **Then** debe abrirse un modal pidiendo la cantidad de horas y materia/tema.

### Scenario 4: Actualización Reactiva (Upsert)
- **Given** que el usuario completa los datos en el modal de "Estudio"
- **When** hace clic en "Guardar"
- **Then** el modal se cierra, la solicitud Upsert se envía al backend, y la tarjeta de "Estudio" cambia a estado "Completado" inmediatamente (gracias a Signals) sin recargar la página.