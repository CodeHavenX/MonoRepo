-- ============================================================================
-- Migration: Fix RLS performance — wrap auth.uid() in SELECT
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/383
-- ============================================================================
-- Wrapping auth.uid() in (SELECT auth.uid()) allows Postgres to cache the
-- result per statement (initPlan), instead of re-evaluating it for each row.
-- Benchmarks show 94-99% query performance improvement at scale.
-- See: https://supabase.com/docs/guides/database/postgres/row-level-security#call-functions-with-select
-- ============================================================================

-- units
DROP POLICY IF EXISTS "org_scoped_units" ON units;
CREATE POLICY "org_scoped_units"
ON units FOR ALL TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
);

-- common_areas
DROP POLICY IF EXISTS "org_scoped_common_areas" ON common_areas;
CREATE POLICY "org_scoped_common_areas"
ON common_areas FOR ALL TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
);

-- tasks
DROP POLICY IF EXISTS "org_scoped_tasks" ON tasks;
CREATE POLICY "org_scoped_tasks"
ON tasks FOR ALL TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
);
