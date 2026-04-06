-- ============================================================================
-- Migration: Remove org_id from common_areas table
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/387
-- ============================================================================
-- This migration:
-- 1. Drops the org_id index from common_areas
-- 2. Drops the org_scoped_common_areas RLS policy (IF EXISTS — already removed
--    by 20260324000001_blanket_deny_rls_new_tables.sql; kept for safety)
-- 3. Removes the org_id column from common_areas
-- org membership is always derivable via property_id, making org_id redundant.
-- Authorization is enforced exclusively in the Kotlin BE layer (service role bypasses RLS).
--
-- NOTE: org_id removal from units, tasks, unit_occupants, rent_config, and
-- payment_records is tracked separately — those tables have composite FK
-- dependencies that require a coordinated multi-table migration.
-- ============================================================================

DROP INDEX IF EXISTS idx_common_areas_org_id;
DROP POLICY IF EXISTS "org_scoped_common_areas" ON common_areas;
ALTER TABLE common_areas DROP COLUMN org_id;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
