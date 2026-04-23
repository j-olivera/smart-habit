-- V8: Normalize habits structure
-- Change: habit-normalization

-- 1. Create habits definition table
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

-- 2. Add habit_id to log tables
-- habit_study
ALTER TABLE habit_study ADD COLUMN habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE;
CREATE INDEX idx_habit_study_habit_id ON habit_study(habit_id);

-- habit_exercise
ALTER TABLE habit_exercise ADD COLUMN habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE;
CREATE INDEX idx_habit_exercise_habit_id ON habit_exercise(habit_id);

-- habit_nutrition
ALTER TABLE habit_nutrition ADD COLUMN habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE;
CREATE INDEX idx_habit_nutrition_habit_id ON habit_nutrition(habit_id);

-- habit_mood
ALTER TABLE habit_mood ADD COLUMN habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE;
CREATE INDEX idx_habit_mood_habit_id ON habit_mood(habit_id);

-- habit_sleep
ALTER TABLE habit_sleep ADD COLUMN habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE;
CREATE INDEX idx_habit_sleep_habit_id ON habit_sleep(habit_id);
