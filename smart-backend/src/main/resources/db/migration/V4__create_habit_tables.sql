-- V4: Create habit tables (study, exercise, nutrition, mood, sleep)
-- Change: infrastructure-setup

-- habit_study
CREATE TABLE habit_study (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    studied BOOLEAN NOT NULL,
    hours INT,
    subject VARCHAR(255),
    skip_reason VARCHAR(500)
);

CREATE INDEX idx_habit_study_entry_id ON habit_study(entry_id);

-- habit_exercise
CREATE TABLE habit_exercise (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    exercised BOOLEAN NOT NULL,
    hours INT,
    muscle_groups VARCHAR(50),
    energy_level INT,
    skip_reason VARCHAR(500)
);

CREATE INDEX idx_habit_exercise_entry_id ON habit_exercise(entry_id);

-- habit_nutrition
CREATE TABLE habit_nutrition (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    rating VARCHAR(50) NOT NULL,
    has_observations BOOLEAN NOT NULL DEFAULT FALSE,
    met_goal BOOLEAN
);

CREATE INDEX idx_habit_nutrition_entry_id ON habit_nutrition(entry_id);

-- habit_mood
CREATE TABLE habit_mood (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    mood VARCHAR(50) NOT NULL,
    has_observations BOOLEAN NOT NULL DEFAULT FALSE,
    event_description VARCHAR(500),
    socialized BOOLEAN NOT NULL DEFAULT FALSE,
    social_with VARCHAR(255)
);

CREATE INDEX idx_habit_mood_entry_id ON habit_mood(entry_id);

-- habit_sleep
CREATE TABLE habit_sleep (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    hours FLOAT NOT NULL,
    quality VARCHAR(50) NOT NULL,
    napped BOOLEAN NOT NULL DEFAULT FALSE,
    nap_hours FLOAT,
    nap_needed BOOLEAN
);

CREATE INDEX idx_habit_sleep_entry_id ON habit_sleep(entry_id);