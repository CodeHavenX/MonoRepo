-- ============================================================================
-- Migration: Tighten RLS on financial tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/418
-- Overrides policies from: 20260204000001_enable_rls_policies.sql
-- ============================================================================
-- 1. Write-protects user_organization_mapping (was FOR ALL USING(true))
-- 2. Adds self-read SELECT on unit_occupants for residents
-- 3. Replaces temporary FOR ALL policies on rent_config and payment_records
--    with granular per-operation policies
--
-- Policy matrix (rent_config, payment_records):
--   SELECT  — active org members + residents (own unit via unit_occupants)
--   INSERT/UPDATE/DELETE — OWNER, ADMIN, MANAGER (ACTIVE) only
--
-- All policies use (SELECT auth.uid()) for initPlan caching.
-- ============================================================================

-- ============================================================================
-- PART 1: Write-protect user_organization_mapping.
-- Replaces the permissive FOR ALL USING(true) policy from 20260204000001.
-- Authenticated users retain SELECT (needed for RBAC subqueries).
-- INSERT/UPDATE/DELETE are restricted to service role, which bypasses RLS.
-- ============================================================================

DROP POLICY IF EXISTS "authenticated_all_user_organization_mapping" ON user_organization_mapping;

CREATE POLICY "select_user_organization_mapping" ON user_organization_mapping
FOR SELECT TO authenticated
USING (true);

-- ============================================================================
-- PART 2: Self-read SELECT on unit_occupants for residents.
-- The existing FOR ALL policy is org-scoped, so residents (not in
-- user_organization_mapping) cannot read their own row. This policy
-- enables the resident subqueries in Parts 4 and 9 below.
-- ============================================================================

CREATE POLICY "select_own_unit_occupant" ON unit_occupants
FOR SELECT TO authenticated
USING (
    user_id = (SELECT auth.uid())
    AND status = 'ACTIVE'
    AND deleted_at IS NULL
);

-- ============================================================================
-- PART 3: Drop temporary FOR ALL policies on financial tables
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_rent_config" ON rent_config;
DROP POLICY IF EXISTS "org_scoped_payment_records" ON payment_records;

-- ============================================================================
-- PART 4: rent_config — SELECT for active org members
-- ============================================================================

CREATE POLICY "select_rent_config" ON rent_config
FOR SELECT TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 5: rent_config — SELECT for residents (own unit only)
-- ============================================================================

CREATE POLICY "select_resident_rent_config" ON rent_config
FOR SELECT TO authenticated
USING (
    unit_id IN (
        SELECT unit_id FROM unit_occupants
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
          AND deleted_at IS NULL
    )
);

-- ============================================================================
-- PART 6: rent_config — INSERT (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "insert_rent_config" ON rent_config
FOR INSERT TO authenticated
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 7: rent_config — UPDATE (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "update_rent_config" ON rent_config
FOR UPDATE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 8: rent_config — DELETE (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "delete_rent_config" ON rent_config
FOR DELETE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 9: payment_records — SELECT for active org members
-- ============================================================================

CREATE POLICY "select_payment_records" ON payment_records
FOR SELECT TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 10: payment_records — SELECT for residents (own unit only)
-- ============================================================================

CREATE POLICY "select_resident_payment_records" ON payment_records
FOR SELECT TO authenticated
USING (
    unit_id IN (
        SELECT unit_id FROM unit_occupants
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
          AND deleted_at IS NULL
    )
);

-- ============================================================================
-- PART 11: payment_records — INSERT (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "insert_payment_records" ON payment_records
FOR INSERT TO authenticated
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 12: payment_records — UPDATE (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "update_payment_records" ON payment_records
FOR UPDATE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);

-- ============================================================================
-- PART 13: payment_records — DELETE (OWNER/ADMIN/MANAGER only)
-- ============================================================================

CREATE POLICY "delete_payment_records" ON payment_records
FOR DELETE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND role IN ('OWNER', 'ADMIN', 'MANAGER')
          AND status = 'ACTIVE'
    )
);
