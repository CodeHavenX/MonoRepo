-- ============================================================================
-- Migration: Remove org_id from tasks table; make property_id NOT NULL
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/387
-- ============================================================================
-- Tasks are property-scoped resources. org_id is derivable via
-- property_id → properties → organizations, so it is redundant.
-- Authorization is enforced exclusively in the Kotlin BE layer.
--
-- This migration also makes property_id NOT NULL and drops the
-- at_least_one_location constraint (which allowed unit_id or common_area_id
-- to stand in for property_id). property_id is now the primary scope anchor;
-- unit_id and common_area_id remain optional sub-scoping fields.
-- ============================================================================

-- Drop the partial index on org_id (created in 20260310000001_create_tasks_table.sql)
DROP INDEX IF EXISTS idx_tasks_org_id;

-- Drop the composite index on (org_id, status)
DROP INDEX IF EXISTS idx_tasks_org_status;

-- Drop the at_least_one_location check constraint before altering columns
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS at_least_one_location;

-- Make property_id NOT NULL (tasks are always scoped to a property)
ALTER TABLE tasks ALTER COLUMN property_id SET NOT NULL;

-- Remove the org_id column
ALTER TABLE tasks DROP COLUMN IF EXISTS org_id;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
