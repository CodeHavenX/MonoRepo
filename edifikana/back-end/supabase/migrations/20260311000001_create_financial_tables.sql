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
-- NOTE: Role-differentiated access (admin write, resident read-own-unit)
-- is enforced at the back-end RBAC layer, not at DB level, because:
--   a) user_organization_mapping has no role column
--   b) unit_occupants table does not exist yet (issue #382)
-- When unit_occupants is implemented, add a resident-scoped SELECT policy.
-- ============================================================================

-- ============================================================================
-- PART 1: ENUMS
-- ============================================================================

CREATE TYPE payment_type   AS ENUM ('RENT', 'HOA', 'UTILITIES', 'OTHER');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE');

-- ============================================================================
-- PART 2: rent_config TABLE
-- ============================================================================

CREATE TABLE rent_config (
    rent_config_id  UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    unit_id         UUID           NOT NULL REFERENCES units(unit_id) ON DELETE CASCADE,
    org_id          UUID           NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    monthly_amount  NUMERIC(10, 2) NOT NULL CHECK (monthly_amount > 0),
    due_day         INT            NOT NULL CHECK (due_day >= 1 AND due_day <= 28),
    currency        TEXT           NOT NULL DEFAULT 'USD',
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_by      UUID           REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMPTZ    DEFAULT NULL
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
    unit_id            UUID           NOT NULL REFERENCES units(unit_id) ON DELETE CASCADE,
    org_id             UUID           NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    payment_type       payment_type   NOT NULL,
    period_month       DATE           NOT NULL,
    amount_due         NUMERIC(10, 2),
    amount_paid        NUMERIC(10, 2),
    status             payment_status NOT NULL DEFAULT 'PENDING',
    due_date           DATE,
    paid_date          DATE,
    recorded_by        UUID           REFERENCES users(id) ON DELETE SET NULL,
    recorded_at        TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    notes              TEXT,
    deleted_at         TIMESTAMPTZ    DEFAULT NULL
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
