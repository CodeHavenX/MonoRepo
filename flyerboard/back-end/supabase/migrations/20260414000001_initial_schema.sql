-- ============================================================================
-- Initial Schema: user_profiles and flyers tables
-- ============================================================================

-- ============================================================================
-- USER_PROFILES TABLE
-- ============================================================================

CREATE TABLE user_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================================
-- FLYERS TABLE
-- ============================================================================

CREATE TABLE flyers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    file_path TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'archived')),
    expires_at TIMESTAMPTZ,
    uploader_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================================
-- INDEXES
-- ============================================================================

CREATE INDEX idx_flyers_status ON flyers(status);

CREATE INDEX idx_flyers_expires_at ON flyers(expires_at)
    WHERE expires_at IS NOT NULL;

CREATE INDEX idx_flyers_created_at ON flyers(created_at DESC);

CREATE INDEX idx_flyers_uploader_id ON flyers(uploader_id);

CREATE INDEX idx_flyers_status_created_at ON flyers(status, created_at DESC);

-- ============================================================================
-- UPDATED_AT TRIGGERS
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_flyers_updated_at
    BEFORE UPDATE ON flyers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
