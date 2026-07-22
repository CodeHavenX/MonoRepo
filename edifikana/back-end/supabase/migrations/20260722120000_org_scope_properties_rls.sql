-- ============================================================================
-- Migration: Org-scope RLS policy for properties
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/549
-- ============================================================================
-- This migration:
-- 1. Drops the blanket-allow "authenticated_all_properties" policy on `properties`
-- 2. Replaces it with an org-scoped policy matching the org_scoped_units /
--    org_scoped_common_areas pattern, so a property row is only visible to
--    authenticated users who are members of that property's organization
-- ============================================================================

DROP POLICY "authenticated_all_properties" ON properties;

-- Policy is scoped by organization_id to prevent cross-tenant data leakage.
-- Users may only access rows belonging to an organization they are a member of.
CREATE POLICY "org_scoped_properties"
ON properties
FOR ALL
TO authenticated
USING (
    organization_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
)
WITH CHECK (
    organization_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
);

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
