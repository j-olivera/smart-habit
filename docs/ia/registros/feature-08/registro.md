# Registro — Feature 08: Dashboard & Sidebar Frontend

## Información

| Campo | Valor |
|---|---|
| Fecha | 2026-04-30 |
| Tipo | Feature Frontend |
| Estado | Implementado ✅ |
| Spec | `docs/ia/specs/front/feature-08.md` |

## Descripción

Implementación del Dashboard interactivo con navegación lateral (Sidebar) para usuarios autenticados. Permite visualizar y registrar los 5 hábitos fijos del sistema y los hábitos personales del usuario.

## Decisiones Técnicas

### 1. Virtual Pending State (sin escritura en DB al inicio del día)
- **Decisión**: No se crean filas en la base de datos al inicio de un nuevo día. El estado `PENDIENTE` es manejado por el Frontend.
- **Razón**: Evita escrituras innecesarias en la DB. El backend devuelve `null` para hábitos no completados (upsert). El Front cruza la lista estática de metadatos con la respuesta del backend para determinar el estado visual.

### 2. Angular Signals para State Management
- **Decisión**: `HabitStateService` usa `signal()` y `computed()` exclusivamente. Sin `BehaviorSubject`.
- **Razón**: Convención del proyecto (definida en `conventions.md`). Más eficiente con Angular's Change Detection (OnPush).

### 3. DashboardLayoutComponent con Child Routes
- **Decisión**: Layout padre en `/app` con rutas hijas (`/app/dashboard`).
- **Razón**: El Sidebar se renderiza una sola vez. No parpadea al navegar entre secciones autenticadas.

### 4. Metadatos de Hábitos en Constante del Front
- **Decisión**: `habit-metadata.constant.ts` aloja los datos estáticos (iconos, colores, descripciones) de los 5 hábitos fijos.
- **Razón**: Estos datos nunca cambian y no justifican una llamada HTTP al backend.

## Archivos Creados / Modificados

| Archivo | Acción |
|---|---|
| `src/app/models/habit/habit.model.ts` | NUEVO — Interfaces TypeScript de todos los logs |
| `src/app/models/habit/habit-metadata.constant.ts` | NUEVO — Metadatos estáticos de los 5 hábitos |
| `src/app/services/habit/habit-state.service.ts` | NUEVO — Servicio reactivo con Signals |
| `src/app/services/habit/habit-state.service.spec.ts` | NUEVO — 14 tests unitarios (Vitest + TestBed) |
| `src/app/components/layout/dashboard-layout/dashboard-layout.component.ts` | NUEVO — Layout contenedor |
| `src/app/components/shared/sidebar/sidebar.component.ts` | NUEVO — Sidebar con nav + logout |
| `src/app/components/shared/sidebar/sidebar.component.html` | NUEVO — Template del Sidebar (glassmorphism) |
| `src/app/components/dashboard/dashboard.component.ts` | NUEVO — Dashboard con computeds (progress ring) |
| `src/app/components/dashboard/dashboard.component.html` | NUEVO — Bento Grid con estados visuales |
| `src/app/components/dashboard/habit-modal/habit-modal.component.ts` | NUEVO — Modal base (esqueleto) |
| `src/app/app.routes.ts` | MODIFICADO — Rutas `/app` con lazy loading |
| `src/app/app.spec.ts` | MODIFICADO — Corregido test boilerplate del CLI |
| `docs/ia/specs/front/feature-08.md` | MODIFICADO — Spec completa con escenarios |

## Tests

- **Runner**: Vitest (via `ng test`)
- **Total**: 14 tests en `habit-state.service.spec.ts`
- **Resultado**: ✅ 14/14 pasando
