-- V9: Refactor habit structures and add personal habits
-- Change: architecture-fix-and-personal-habits

-- 1. Drop habit_id from fixed log tables
ALTER TABLE habit_study DROP COLUMN habit_id;
ALTER TABLE habit_exercise DROP COLUMN habit_id;
ALTER TABLE habit_nutrition DROP COLUMN habit_id;
ALTER TABLE habit_mood DROP COLUMN habit_id;
ALTER TABLE habit_sleep DROP COLUMN habit_id;

-- 2. Create personal habit logs table
CREATE TABLE habit_personal (
    id BIGSERIAL PRIMARY KEY,
    entry_id BIGINT NOT NULL REFERENCES daily_entries(id) ON DELETE CASCADE,
    habit_id BIGINT NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    completed BOOLEAN NOT NULL,
    hours FLOAT,
    description VARCHAR(500)
);

CREATE INDEX idx_habit_personal_entry_id ON habit_personal(entry_id);
CREATE INDEX idx_habit_personal_habit_id ON habit_personal(habit_id);
