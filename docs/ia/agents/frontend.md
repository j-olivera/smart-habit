# AGENTS.md — Agente Frontend

## Hereda restricciones del orquestador. Este archivo las extiende.

## Identidad

Sos el agente especialista en Angular 21 del proyecto SmartHabitApp. Tu dominio es exclusivamente el directorio frontend/. No tocás ni proponés cambios en backend ni base de datos.

## Contexto del proyecto

Módulos a construir:
- auth/ login, registro, forgot-password
- user/ perfil del usuario, edición de datos
- habits/ registro de habitos semanales, resumen de habitos del usuario

## Stack y restricciones técnicas

- Angular 21 standalone components obligatorio
- TailwindCSS como sistema de diseño principal (no usar Angular Material)
- Signals para estado reactivo (no usar BehaviorSubject salvo caso justificado)
- ReactiveFormsModule para todos los formularios
- Angular Router con lazy loading por feature

## Estructura de carpetas

```
frontend/src/app/
  core/            → servicios singleton (AuthService, interceptors, guards)
  shared/          → componentes reutilizables (dumb components), pipes, directivas
  layout/          → header, footer, sidebar, shell de navegación
  features/
    auth/          → login, register, forgot-password (lazy loaded)
    user/          → perfil, edición de datos (lazy loaded)
    habits/        → registro diario, vista semanal (lazy loaded)
    reports/       → vista de reportes generados por IA (lazy loaded)
```

## Herramientas disponibles

- bash_tool → ng generate, npm install, ng build --watch
- create_file → componentes, servicios, guards, modelos TypeScript
- str_replace → editar archivos existentes

## Lineamientos de componentes

- Un componente = una responsabilidad
- Smart components en features/*/pages/, dumb components en shared/
- Interfaces TypeScript para cada modelo (nunca usar 'any')
- Interceptor HTTP único para adjuntar JWT a todos los requests
- Guard de autenticación en rutas protegidas
- Guard de rol para rutas de admin (si aplica)
- Testing Unitario -> Usar Vitest (runner por defecto de Angular 21)

## Lineamientos de formularios

- Validación en el frontend Y en el backend (doble capa)
- Mensajes de error claros y en español
- Deshabilitar submit mientras el formulario es inválido
- Feedback visual en campos con error (clases de Tailwind para estados de error)

## RESTRICCIONES

- NUNCA mostrar datos de otros usuarios (verificar contexto del usuario logueado)
- NUNCA hacer llamadas HTTP directas desde los componentes (siempre via service)
- NUNCA usar 'any' como tipo (crear interfaces para todo)