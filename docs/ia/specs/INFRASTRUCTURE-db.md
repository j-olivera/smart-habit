# Spec: Database Schema - Smart Habit

## Meta
- **Change**: habit-normalization
- **Component**: Database Schema
- **Version**: 1.2
- **Status**: Approved
- **ENUM Strategy**: VARCHAR(50) (Consistente con implementación actual)
- **Migrations**: Separadas por tabla

---

## 1. Tipos y Constantes

Se utilizará `VARCHAR(50)` para representar enums en la base de datos para mantener flexibilidad y consistencia con las migraciones iniciales. Los valores permitidos se validarán en la capa de aplicación.

---

## 2. Tablas

### 2.1 users

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active) WHERE active = TRUE;
```

### 2.2 habits (NUEVA v1.2)

Define los hábitos que un usuario ha decidido trackear.

```sql
CREATE TABLE habits (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- e.g. 'STUDY', 'EXERCISE', 'NUTRITION', 'MOOD', 'SLEEP'
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_habits_user_id ON habits(user_id);
```

### 2.3 refresh_tokens

```sql
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_active ON refresh_tokens(expires_at) WHERE revoked = FALSE;
```

### 2.4 daily_entries

```sql
CREATE TABLE daily_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, date)
);

CREATE INDEX idx_daily_entries_user_date ON daily_entries(user_id, date);
CREATE INDEX idx_daily_entries_user_created ON daily_entries(user_id, created_at);
```

### 2.5 habit_study

```sql
CREATE TABLE habit_study (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE, -- NUEVA v1.2
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    studied BOOLEAN NOT NULL,
    hours INT CHECK (hours IS NULL OR (hours >= 0 AND hours <= 24)),
    subject VARCHAR(255),
    skip_reason VARCHAR(500)
);

CREATE INDEX idx_habit_study_habit_id ON habit_study(habit_id);
CREATE INDEX idx_habit_study_entry_id ON habit_study(entry_id);
```

### 2.6 habit_exercise

```sql
CREATE TABLE habit_exercise (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE, -- NUEVA v1.2
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    exercised BOOLEAN NOT NULL,
    hours INT CHECK (hours IS NULL OR (hours >= 0 AND hours <= 24)),
    muscle_groups VARCHAR(50),
    energy_level INT CHECK (energy_level IS NULL OR (energy_level >= 1 AND energy_level <= 100)),
    skip_reason VARCHAR(500)
);

CREATE INDEX idx_habit_exercise_habit_id ON habit_exercise(habit_id);
CREATE INDEX idx_habit_exercise_entry_id ON habit_exercise(entry_id);
```

### 2.7 habit_nutrition

```sql
CREATE TABLE habit_nutrition (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE, -- NUEVA v1.2
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    rating VARCHAR(50) NOT NULL,
    has_observations BOOLEAN NOT NULL DEFAULT FALSE,
    met_goal BOOLEAN
);

CREATE INDEX idx_habit_nutrition_habit_id ON habit_nutrition(habit_id);
CREATE INDEX idx_habit_nutrition_entry_id ON habit_nutrition(entry_id);
```

### 2.8 habit_mood

```sql
CREATE TABLE habit_mood (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE, -- NUEVA v1.2
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    mood VARCHAR(50) NOT NULL,
    has_observations BOOLEAN NOT NULL DEFAULT FALSE,
    event_description VARCHAR(500),
    socialized BOOLEAN NOT NULL DEFAULT FALSE,
    social_with VARCHAR(255)
);

CREATE INDEX idx_habit_mood_habit_id ON habit_mood(habit_id);
CREATE INDEX idx_habit_mood_entry_id ON habit_mood(entry_id);
```

### 2.9 habit_sleep

```sql
CREATE TABLE habit_sleep (
    id BIGSERIAL PRIMARY KEY,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE, -- NUEVA v1.2
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    hours FLOAT NOT NULL CHECK (hours >= 0 AND hours <= 24),
    quality VARCHAR(50) NOT NULL,
    napped BOOLEAN NOT NULL DEFAULT FALSE,
    nap_hours FLOAT CHECK (nap_hours IS NULL OR (nap_hours >= 0 AND nap_hours <= 24)),
    nap_needed BOOLEAN
);

CREATE INDEX idx_habit_sleep_habit_id ON habit_sleep(habit_id);
CREATE INDEX idx_habit_sleep_entry_id ON habit_sleep(entry_id);
```

### 2.10 weekly_reports

```sql
CREATE TABLE weekly_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start DATE NOT NULL,
    week_end DATE NOT NULL,
    ai_content TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, week_start)
);

CREATE INDEX idx_weekly_reports_user_week ON weekly_reports(user_id, week_start);
```

---

## 3. Migraciones (Flyway)

Orden de ejecución:

| # | Archivo | Descripción |
|---|---------|-------------|
| V1 | V1__create_users_table.sql | Tabla users |
| V2 | V2__create_refresh_tokens_table.sql | Tabla refresh_tokens |
| V3 | V3__create_daily_entries_table.sql | Tabla daily_entries |
| V4 | V4__create_habit_tables.sql | Estructura inicial de hábitos |
| V5 | V5__create_weekly_reports_table.sql | Tabla weekly_reports |
| V6 | V6__create_indexes.sql | Índices adicionales |
| V7 | V7__add_name_to_users_table.sql | Columna name en users |
| V8 | V8__normalize_habits_structure.sql | **NUEVA (v1.2)**: Tabla habits y normalización |

---

## 4. Validaciones

| Validación | Sentencia |
|------------|-----------|
| UNIQUE email en users | ✅ |
| UNIQUE (user_id, date) en daily_entries | ✅ |
| UNIQUE (user_id, week_start) en weekly_reports | ✅ |
| FK con CASCADE en todas las relaciones | ✅ |
| CHECK constraints en ranges (hours, energy_level) | ✅ |
| ENUM types para valores fijos | ✅ |

---

## 5. Criterios de Éxito

- [ ] Todas las migraciones se ejecutan sin errores
- [ ] Flyway registra las 10 migraciones en flyway_schema_history
- [ ] Todas las FK funcionan correctamente (testear con CASCADE)
- [ ] Los ENUM types aceptan solo valores válidos
- [ ] Los índices mejoran performance de queries típicas