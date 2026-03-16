-- ============================================================================
-- Migration: Create unit_occupants table
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/382
-- Depends on: 20260309000001_create_units_and_common_areas.sql
--             20260311000001_create_financial_tables.sql (units_unit_id_org_id_unique)
-- ============================================================================
-- This migration:
-- 1. Adds owner_user_id column to units
-- 2. Creates occupant_type and occupancy_status enums
-- 3. Creates unit_occupants table with composite FK (unit_id, org_id)
-- 4. Adds indexes and partial indexes (soft-delete aware)
-- 5. Enables RLS with org-scoped policy using (SELECT auth.uid()) for performance
-- ============================================================================
-- NOTE: The current RLS policy grants FOR ALL access to any authenticated org member.
-- This is intentionally temporary. The target access model is:
--   - INSERT/UPDATE/DELETE: Owner, Admin, Manager roles only
--   - SELECT (all org records): Owner, Admin, Manager
--   - SELECT (own unit only): Resident (self-read via unit_occupants.user_id)
--
-- Two schema prerequisites must land before this can be tightened at the DB level:
--   a) #418 — user_organization_mapping needs a role column (currently has none).
--      Once available, split the FOR ALL policy into separate write (role-gated)
--      and read policies.
--   b) Resident self-read policy can be added once #418 lands and the role column
--      is available to gate write access separately.
--
-- Until #418 lands, role enforcement is handled exclusively by the back-end
-- RBAC service (existing pattern across the codebase).
-- ============================================================================

-- ============================================================================
-- PART 1: Add owner_user_id to units
-- Tracks legal ownership independently of physical occupancy.
-- Nullable — a unit may not yet have an assigned owner.
-- ============================================================================

ALTER TABLE units ADD COLUMN owner_user_id UUID REFERENCES users(id) ON DELETE SET NULL;

-- ============================================================================
-- PART 2: ENUMS
-- ============================================================================

CREATE TYPE occupant_type    AS ENUM ('TENANT', 'RESIDENT');
CREATE TYPE occupancy_status AS ENUM ('ACTIVE', 'INACTIVE');

-- ============================================================================
-- PART 3: unit_occupants TABLE
-- ============================================================================

CREATE TABLE unit_occupants (
    occupant_id    UUID              PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id        UUID              NOT NULL,
    org_id         UUID              NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id        UUID              REFERENCES users(id) ON DELETE SET NULL,
    added_by       UUID              REFERENCES users(id) ON DELETE SET NULL,
    occupant_type  occupant_type     NOT NULL,
    is_primary     BOOLEAN           NOT NULL DEFAULT FALSE,
    start_date     DATE              NOT NULL,
    end_date       DATE,
    status         occupancy_status  NOT NULL DEFAULT 'ACTIVE',
    added_at       TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMPTZ       DEFAULT NULL,

    -- Composite FK ensures unit_id and org_id refer to the same row in units,
    -- preventing cross-tenant data corruption (a user cannot reference a unit
    -- from a different org even if their org_id passes RLS).
    -- Requires units_unit_id_org_id_unique constraint from 20260311000001.
    CONSTRAINT unit_occupants_unit_org_fk
        FOREIGN KEY (unit_id, org_id) REFERENCES units(unit_id, org_id) ON DELETE CASCADE
);

-- ============================================================================
-- PART 4: INDEXES
-- ============================================================================

-- One ACTIVE occupancy per user per org (a person lives in one unit at a time).
-- NULL user_id excluded: non-app occupants (name-only records) don't count against this limit.
CREATE UNIQUE INDEX IF NOT EXISTS idx_unit_occupants_one_active_per_user
    ON unit_occupants(user_id, org_id)
    WHERE status = 'ACTIVE' AND user_id IS NOT NULL AND deleted_at IS NULL;

-- One ACTIVE occupancy record per (unit_id, user_id) pair.
CREATE UNIQUE INDEX IF NOT EXISTS idx_unit_occupants_active_unit_user
    ON unit_occupants(unit_id, user_id)
    WHERE status = 'ACTIVE' AND user_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_unit_occupants_not_deleted
    ON unit_occupants(deleted_at) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_unit_occupants_org_id
    ON unit_occupants(org_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_unit_occupants_unit_id
    ON unit_occupants(unit_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_unit_occupants_user_id
    ON unit_occupants(user_id) WHERE deleted_at IS NULL;

-- Primary access pattern: list all active occupants in a unit.
CREATE INDEX IF NOT EXISTS idx_unit_occupants_unit_status
    ON unit_occupants(unit_id, status) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 5: RLS — org-scoped, using (SELECT auth.uid()) per Supabase perf docs
-- See: https://supabase.com/docs/guides/database/postgres/row-level-security#call-functions-with-select
-- ============================================================================

ALTER TABLE unit_occupants ENABLE ROW LEVEL SECURITY;

CREATE POLICY "org_scoped_unit_occupants"
ON unit_occupants
FOR ALL
TO authenticated
USING (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = (SELECT auth.uid())
    )
);
