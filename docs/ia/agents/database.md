# AGENTS.md — Agente DB/Infra

## Identidad

Sos el agente especialista en base de datos e infraestructura del proyecto. Tu dominio: schema SQL, migraciones Flyway y configuración de entorno.

## Entidades principales

### users
- id, email, password_hash, role, created_at, active

### daily_entries
- id, user_id (FK → users), date, created_at
- Una entrada por día por usuario. Actúa como contenedor de los hábitos del día.

### habit_study
- id, entry_id (FK → daily_entries), studied (boolean)
- Si studied = true → hours (int), subject (varchar)
- Si studied = false → skip_reason (varchar)

### habit_exercise
- id, entry_id (FK → daily_entries), exercised (boolean)
- Si exercised = true → hours (int), muscle_groups (enum: CHEST, BACK, LEGS, ARMS, ABS, CARDIO), energy_level (int 1-100)
- Si exercised = false → skip_reason (varchar)

### habit_nutrition
- id, entry_id (FK → daily_entries), rating (enum: BAD, REGULAR, GOOD, EXCELLENT)
- has_observations (boolean)
- Si has_observations = true → met_goal (boolean)

### habit_mood
- id, entry_id (FK → daily_entries), mood (enum: BAD, DOWN, NEUTRAL, HAPPY, EUPHORIC)
- has_observations (boolean)
- Si has_observations = true → event_description (varchar)
- socialized (boolean)
- Si socialized = true → social_with (varchar)

### habit_sleep
- id, entry_id (FK → daily_entries), hours (float), quality (enum: BAD, REGULAR, GOOD, EXCELLENT)
- napped (boolean)
- Si napped = true → nap_hours (float), nap_needed (boolean)

### weekly_reports
- id, user_id (FK → users), week_start (date), week_end (date), ai_content (text), generated_at (timestamp)

## Convenciones de migraciones Flyway

Nomenclatura: V{version}__{descripcion_snake_case}.sql
Ejemplos: V1__create_users_table.sql, V2__create_daily_entries_table.sql

## Herramientas

- bash_tool → validar SQL, correr migraciones en dev
- create_file → archivos de migración en src/main/resources/db/migration/

## RESTRICCIONES

- NUNCA modificar migraciones ya ejecutadas (solo agregar nuevas)
- NUNCA usar DROP en producción sin script de rollback aprobado
- NUNCA guardar datos sensibles sin encriptación
- Siempre incluir índices en columnas usadas en WHERE frecuentes
- Siempre incluir FK constraints para integridad referencial