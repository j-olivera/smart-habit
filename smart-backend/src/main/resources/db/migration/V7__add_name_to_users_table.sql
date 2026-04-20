-- V7: Add name to users table
-- Change: user-register-backend

ALTER TABLE users ADD COLUMN name VARCHAR(255);

-- Prevent constraint violation on existing rows
UPDATE users SET name = 'Usuario' WHERE name IS NULL;

-- Enforce NOT NULL
ALTER TABLE users ALTER COLUMN name SET NOT NULL;
