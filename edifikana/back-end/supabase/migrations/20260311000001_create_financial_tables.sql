-- ============================================================================
-- Migration: Create rent_config and payment_records tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/383
-- Depends on: 20260309000001_create_units_and_common_areas.sql
-- ============================================================================
-- This migration:
-- 1. Creates payment_type and payment_status enums
-- 2. Creates rent_config table (one active config per unit)
-- 3. Creates payment_records table (financial transaction log per unit/month)
-- 4. Adds indexes and partial indexes (soft-delete aware)
-- 5. Enables RLS with org-scoped policy using (SELECT auth.uid()) for performance
-- ============================================================================
-- NOTE: The current RLS policy grants FOR ALL access to any authenticated org member.
-- This is intentionally temporary. The target access model is:
--   - INSERT/UPDATE/DELETE: Owner, Admin, Manager roles only
--   - SELECT (all org records): Owner, Admin, Manager
--   - SELECT (own unit only): Resident (via unit_occupants join)
--
-- Two schema prerequisites must land before this can be tightened at the DB level:
--   a) #418 — user_organization_mapping needs a role column (currently has none).
--      Once available, split the FOR ALL policy into separate write (role-gated)
--      and read policies for rent_config and payment_records.
--   b) #382 — unit_occupants table must exist before a resident-scoped SELECT
--      policy can be added.
--
-- Until #418 lands, role enforcement is handled exclusively by the back-end
-- RBAC service (existing pattern across the codebase). This is tracked as a
-- sub-task of #418.
-- ============================================================================

-- ============================================================================
-- PART 1: ENUMS
-- ============================================================================

CREATE TYPE payment_type   AS ENUM ('RENT', 'HOA', 'UTILITIES', 'OTHER');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE');

-- ============================================================================
-- PART 1b: COMPOSITE UNIQUE ON units
-- Required to support composite FK (unit_id, org_id) references from financial
-- tables, ensuring a row cannot reference a unit that belongs to a different org.
-- unit_id is already the PK so this constraint is trivially satisfied — its only
-- purpose is to create the index Postgres needs for the composite FK target.
-- ============================================================================

ALTER TABLE units ADD CONSTRAINT units_unit_id_org_id_unique UNIQUE (unit_id, org_id);

-- ============================================================================
-- PART 2: rent_config TABLE
-- ============================================================================

CREATE TABLE rent_config (
    rent_config_id  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id         UUID           NOT NULL,
    org_id          UUID           NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    monthly_amount  NUMERIC(10, 2) NOT NULL CHECK (monthly_amount > 0),
    due_day         INT            NOT NULL CHECK (due_day >= 1 AND due_day <= 28),
    currency        TEXT           NOT NULL DEFAULT 'USD',
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_by      UUID           REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ    DEFAULT NULL,
    -- Composite FK ensures unit_id and org_id refer to the same row in units,
    -- preventing cross-tenant data corruption (a user cannot reference a unit
    -- from a different org even if their org_id passes RLS).
    CONSTRAINT rent_config_unit_org_fk
        FOREIGN KEY (unit_id, org_id) REFERENCES units(unit_id, org_id) ON DELETE CASCADE
);

-- Enforce one active rent config per unit (soft-delete aware)
CREATE UNIQUE INDEX IF NOT EXISTS idx_rent_config_unit_unique
    ON rent_config(unit_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_rent_config_not_deleted
    ON rent_config(deleted_at) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_rent_config_org_id
    ON rent_config(org_id) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 3: payment_records TABLE
-- ============================================================================

CREATE TABLE payment_records (
    payment_record_id  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id            UUID           NOT NULL,
    org_id             UUID           NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    payment_type       payment_type   NOT NULL,
    period_month       DATE           NOT NULL,
    amount_due         NUMERIC(10, 2) CHECK (amount_due >= 0),
    amount_paid        NUMERIC(10, 2) CHECK (amount_paid >= 0),
    status             payment_status NOT NULL DEFAULT 'PENDING',
    due_date           DATE,
    paid_date          DATE,
    recorded_by        UUID           REFERENCES users(id) ON DELETE SET NULL,
    recorded_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    notes              TEXT,
    deleted_at         TIMESTAMPTZ    DEFAULT NULL,
    -- period_month must always be the first day of the month (e.g. 2026-03-01).
    -- date_trunc truncates to midnight of the 1st; equality rejects mid-month dates.
    CONSTRAINT payment_records_period_month_first_of_month
        CHECK (date_trunc('month', period_month) = period_month),
    -- Composite FK ensures unit_id and org_id refer to the same row in units,
    -- preventing cross-tenant data corruption.
    CONSTRAINT payment_records_unit_org_fk
        FOREIGN KEY (unit_id, org_id) REFERENCES units(unit_id, org_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_payment_records_not_deleted
    ON payment_records(deleted_at) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_payment_records_org_id
    ON payment_records(org_id) WHERE deleted_at IS NULL;

-- Composite index for the primary access pattern (unit's history by month)
CREATE INDEX IF NOT EXISTS idx_payment_records_unit_period
    ON payment_records(unit_id, period_month) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 4: RLS — org-scoped, using (SELECT auth.uid()) per Supabase perf docs
-- See: https://supabase.com/docs/guides/database/postgres/row-level-security#call-functions-with-select
-- ============================================================================

ALTER TABLE rent_config     ENABLE ROW LEVEL SECURITY;
ALTER TABLE payment_records ENABLE ROW LEVEL SECURITY;

CREATE POLICY "org_scoped_rent_config"
ON rent_config
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

CREATE POLICY "org_scoped_payment_records"
ON payment_records
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
