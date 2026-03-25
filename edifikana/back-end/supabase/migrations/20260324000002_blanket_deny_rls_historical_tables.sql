-- ============================================================================
-- Migration: #457 — Blanket deny RLS on historical tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/457
-- ============================================================================
-- Drops all permissive RLS policies that were added to existing tables before
-- the blanket deny approach was adopted. RLS remains ENABLED on all tables —
-- the absence of permissive policies enforces deny-all behavior for any
-- connection that is not the service role.
--
-- The backend server accesses Supabase via the service role key, which
-- bypasses RLS entirely. All authorization enforcement lives exclusively
-- in the Kotlin BE layer.
--
-- Policy origins:
--   20260204000001_enable_rls_policies.sql
--     — authenticated_all_* policies (FOR ALL USING(true)) on all tables listed
--   20260317000003_tighten_financial_rls.sql
--     — dropped authenticated_all_user_organization_mapping (already gone)
--     — added select_user_organization_mapping (SELECT USING(true))
--   20260317000004_membership_view_rpc_and_security_fix.sql
--     — added owner_insert/update/delete_user_organization_mapping
--
-- Tables affected:
--   employee
--   event_log_entries
--   global_perm_override
--   invites
--   notifications
--   organizations
--   properties
--   time_card_events
--   user_property_mapping
--   users
--   user_organization_mapping
-- ============================================================================

-- ============================================================================
-- PART 1: employee
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_employee" ON employee;

-- ============================================================================
-- PART 2: event_log_entries
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_event_log_entries" ON event_log_entries;

-- ============================================================================
-- PART 3: global_perm_override
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_global_perm_override" ON global_perm_override;

-- ============================================================================
-- PART 4: invites
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_invites" ON invites;

-- ============================================================================
-- PART 5: notifications
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_notifications" ON notifications;

-- ============================================================================
-- PART 6: organizations
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_organizations" ON organizations;

-- ============================================================================
-- PART 7: properties
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_properties" ON properties;

-- ============================================================================
-- PART 8: time_card_events
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_time_card_events" ON time_card_events;

-- ============================================================================
-- PART 9: user_property_mapping
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_user_property_mapping" ON user_property_mapping;

-- ============================================================================
-- PART 10: users
-- Policy added in: 20260204000001_enable_rls_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_users" ON users;

-- ============================================================================
-- PART 11: user_organization_mapping
-- authenticated_all_user_organization_mapping was already dropped in
--   20260317000003_tighten_financial_rls.sql (IF EXISTS is safe guard).
-- select_user_organization_mapping added in 20260317000003.
-- owner_insert/update/delete added in 20260317000004_membership_view_rpc_and_security_fix.sql.
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_user_organization_mapping" ON user_organization_mapping;
DROP POLICY IF EXISTS "select_user_organization_mapping"             ON user_organization_mapping;
DROP POLICY IF EXISTS "owner_insert_user_organization_mapping"       ON user_organization_mapping;
DROP POLICY IF EXISTS "owner_update_user_organization_mapping"       ON user_organization_mapping;
DROP POLICY IF EXISTS "owner_delete_user_organization_mapping"       ON user_organization_mapping;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
