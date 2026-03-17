-- ============================================================================
-- Migration: Tighten RLS on financial tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/418
-- ============================================================================
-- This migration:
-- 1. Drops the temporary FOR ALL org-scoped policies on rent_config and
--    payment_records (added in 20260311000001_create_financial_tables.sql)
-- 2. Replaces them with granular per-operation policies now that
--    user_organization_mapping has a validated role enum and status column
--    (added in 20260317000001_alter_user_org_mapping.sql)
--
-- Policy matrix:
--   SELECT  — all active org members + residents (own unit via unit_occupants)
--   INSERT  — OWNER, ADMIN, MANAGER (active) only
--   UPDATE  — OWNER, ADMIN, MANAGER (active) only
--   DELETE  — OWNER, ADMIN, MANAGER (active) only
--   EMPLOYEE role gets read-only access (covered by SELECT policy, no write policy)
--
-- All policies use (SELECT auth.uid()) for initPlan caching per Supabase RLS
-- performance best practices.
-- ============================================================================

-- ============================================================================
-- PART 1: Drop temporary FOR ALL policies
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_rent_config" ON rent_config;
DROP POLICY IF EXISTS "org_scoped_payment_records" ON payment_records;

-- ============================================================================
-- PART 2: rent_config — SELECT for active org members
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
-- PART 3: rent_config — SELECT for residents (own unit only)
-- Residents are not in user_organization_mapping; access is granted via
-- unit_occupants. unit_occupants.status uses OccupancyStatus enum (ACTIVE).
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
-- PART 4: rent_config — INSERT (OWNER/ADMIN/MANAGER only)
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
-- PART 5: rent_config — UPDATE (OWNER/ADMIN/MANAGER only)
-- USING filters which rows can be targeted; WITH CHECK validates the new values.
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
-- PART 6: rent_config — DELETE (OWNER/ADMIN/MANAGER only)
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
-- PART 7: payment_records — SELECT for active org members
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
-- PART 8: payment_records — SELECT for residents (own unit only)
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
-- PART 9: payment_records — INSERT (OWNER/ADMIN/MANAGER only)
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
-- PART 10: payment_records — UPDATE (OWNER/ADMIN/MANAGER only)
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
-- PART 11: payment_records — DELETE (OWNER/ADMIN/MANAGER only)
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
