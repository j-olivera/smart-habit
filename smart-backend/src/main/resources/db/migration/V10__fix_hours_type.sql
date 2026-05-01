-- V10: Fix hours type in habit_study and habit_exercise
-- Change: fix-schema-types

ALTER TABLE habit_study ALTER COLUMN hours TYPE FLOAT;
ALTER TABLE habit_exercise ALTER COLUMN hours TYPE FLOAT;
