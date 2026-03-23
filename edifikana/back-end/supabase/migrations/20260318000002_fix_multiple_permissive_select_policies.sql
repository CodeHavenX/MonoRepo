-- ============================================================================
-- Migration: Fix multiple permissive SELECT policies (lint 0006)
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/426
-- ============================================================================
-- Supabase advisor lint 0006 flags tables that have more than one permissive
-- SELECT policy for the same role. Postgres evaluates all permissive policies
-- with OR logic, so having two separate SELECT policies is functionally correct
-- but causes both to be evaluated on every row (performance) and is flagged as
-- unintentional. The fix is to merge each pair into a single SELECT policy
-- using an explicit OR condition.
--
-- Tables affected:
--   1. unit_occupants  — org_scoped_unit_occupants (FOR ALL) + select_own_unit_occupant
--   2. rent_config     — select_rent_config + select_resident_rent_config
--   3. payment_records — select_payment_records + select_resident_payment_records
-- ============================================================================

-- ============================================================================
-- PART 1: unit_occupants
-- Drop the FOR ALL policy (which implicitly covered SELECT) and the separate
-- self-read SELECT policy. Replace with:
--   - One combined SELECT policy (org member OR own active row)
--   - Explicit INSERT / UPDATE / DELETE policies preserving the original
--     org-scoped write access
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_unit_occupants" ON unit_occupants;
DROP POLICY IF EXISTS "select_own_unit_occupant" ON unit_occupants;

CREATE POLICY "select_unit_occupants"
ON unit_occupants
FOR SELECT TO authenticated
USING (
    -- Org members see all rows in their org
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
        AND status = 'ACTIVE'
    )
    OR
    -- Residents can read their own active occupant row
    (
        user_id = (SELECT auth.uid())
        AND status = 'ACTIVE'
        AND deleted_at IS NULL
    )
);

CREATE POLICY "insert_unit_occupants"
ON unit_occupants
FOR INSERT TO authenticated
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
        AND status = 'ACTIVE'
    )
);

CREATE POLICY "update_unit_occupants"
ON unit_occupants
FOR UPDATE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
        AND status = 'ACTIVE'
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
        AND status = 'ACTIVE'
    )
);

CREATE POLICY "delete_unit_occupants"
ON unit_occupants
FOR DELETE TO authenticated
USING (
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
);

-- ============================================================================
-- PART 2: rent_config
-- Merge select_rent_config (org members) and select_resident_rent_config
-- (residents via unit_occupants) into a single SELECT policy.
-- ============================================================================

DROP POLICY IF EXISTS "select_rent_config" ON rent_config;
DROP POLICY IF EXISTS "select_resident_rent_config" ON rent_config;

CREATE POLICY "select_rent_config"
ON rent_config
FOR SELECT TO authenticated
USING (
    -- Active org members see all rent configs in their org
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
    )
    OR
    -- Residents see rent configs for their own unit
    unit_id IN (
        SELECT unit_id FROM unit_occupants
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
          AND deleted_at IS NULL
    )
);

-- ============================================================================
-- PART 3: payment_records
-- Merge select_payment_records (org members) and select_resident_payment_records
-- (residents via unit_occupants) into a single SELECT policy.
-- ============================================================================

DROP POLICY IF EXISTS "select_payment_records" ON payment_records;
DROP POLICY IF EXISTS "select_resident_payment_records" ON payment_records;

CREATE POLICY "select_payment_records"
ON payment_records
FOR SELECT TO authenticated
USING (
    -- Active org members see all payment records in their org
    org_id IN (
        SELECT organization_id FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
    )
    OR
    -- Residents see payment records for their own unit
    unit_id IN (
        SELECT unit_id FROM unit_occupants
        WHERE user_id = (SELECT auth.uid())
          AND status = 'ACTIVE'
          AND deleted_at IS NULL
    )
);
