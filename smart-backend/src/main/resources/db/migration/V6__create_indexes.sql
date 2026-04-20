-- V6: Additional indexes
-- Change: infrastructure-setup
-- Note: All indexes already created in respective tables V1-V5

-- Additional composite index for weekly queries
CREATE INDEX IF NOT EXISTS idx_daily_entries_user_date ON daily_entries(user_id, date);

-- Index for refresh token cleanup queries
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires ON refresh_tokens(expires_at) WHERE revoked = FALSE;