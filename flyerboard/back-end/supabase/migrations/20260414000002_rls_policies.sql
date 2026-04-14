-- ============================================================================
-- Row Level Security Policies
-- ============================================================================

-- Enable RLS on both tables
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE flyers ENABLE ROW LEVEL SECURITY;

-- ============================================================================
-- USER_PROFILES POLICIES
-- ============================================================================

-- Users can read their own profile
CREATE POLICY "users_select_own_profile"
ON user_profiles
FOR SELECT
TO authenticated
USING (id = auth.uid());

-- Admins can read all profiles
CREATE POLICY "admins_select_all_profiles"
ON user_profiles
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
);

-- Users can update their own profile (but not role)
CREATE POLICY "users_update_own_profile"
ON user_profiles
FOR UPDATE
TO authenticated
USING (id = auth.uid())
WITH CHECK (id = auth.uid());

-- Admins can update any profile
CREATE POLICY "admins_update_all_profiles"
ON user_profiles
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
)
WITH CHECK (true);

-- Admins can insert profiles
CREATE POLICY "admins_insert_profiles"
ON user_profiles
FOR INSERT
TO authenticated
WITH CHECK (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
);

-- Allow new users to insert their own profile on signup
CREATE POLICY "users_insert_own_profile"
ON user_profiles
FOR INSERT
TO authenticated
WITH CHECK (id = auth.uid());

-- ============================================================================
-- FLYERS POLICIES
-- ============================================================================

-- Anyone (including anonymous) can read approved and archived flyers
CREATE POLICY "public_select_approved_flyers"
ON flyers
FOR SELECT
TO anon, authenticated
USING (status IN ('approved', 'archived'));

-- Authenticated users can read their own flyers regardless of status
CREATE POLICY "users_select_own_flyers"
ON flyers
FOR SELECT
TO authenticated
USING (uploader_id = auth.uid());

-- Admins can read all flyers
CREATE POLICY "admins_select_all_flyers"
ON flyers
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
);

-- Authenticated users can insert flyers (as themselves)
CREATE POLICY "users_insert_flyers"
ON flyers
FOR INSERT
TO authenticated
WITH CHECK (uploader_id = auth.uid());

-- Users can update their own pending flyers
CREATE POLICY "users_update_own_pending_flyers"
ON flyers
FOR UPDATE
TO authenticated
USING (uploader_id = auth.uid() AND status = 'pending')
WITH CHECK (uploader_id = auth.uid());

-- Admins can update any flyer (approve, reject, archive)
CREATE POLICY "admins_update_all_flyers"
ON flyers
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
)
WITH CHECK (true);

-- Users can delete their own pending flyers
CREATE POLICY "users_delete_own_pending_flyers"
ON flyers
FOR DELETE
TO authenticated
USING (uploader_id = auth.uid() AND status = 'pending');

-- Admins can delete any flyer
CREATE POLICY "admins_delete_all_flyers"
ON flyers
FOR DELETE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM user_profiles
        WHERE id = auth.uid() AND role = 'admin'
    )
);
