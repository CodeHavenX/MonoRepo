-- ============================================================================
-- USERS TABLE
-- ============================================================================
-- Stores the display name of registered users, separate from `user_profiles`
-- (which stores the role used for authorization and is auto-created by the
-- back-end on first authenticated request).

CREATE TABLE users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- USERS POLICIES
-- ============================================================================

ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Users can read their own row
CREATE POLICY "users_select_own_user"
ON users
FOR SELECT
TO authenticated
USING (id = auth.uid());

-- Users can insert their own row (registering their display name)
CREATE POLICY "users_insert_own_user"
ON users
FOR INSERT
TO authenticated
WITH CHECK (id = auth.uid());

-- Users can update their own row
CREATE POLICY "users_update_own_user"
ON users
FOR UPDATE
TO authenticated
USING (id = auth.uid())
WITH CHECK (id = auth.uid());
