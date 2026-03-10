-- ============================================================================
-- Migration: Create units and common_areas tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/381
-- ============================================================================
-- This migration:
-- 1. Creates the `units` table
-- 2. Creates the `common_areas` table
-- 3. Adds partial indexes on deleted_at IS NULL for both tables
-- 4. Adds indexes on org_id and property_id for efficient scoped queries
-- 5. Enables RLS with org-scoped policies on both tables
-- ============================================================================

-- ============================================================================
-- PART 1: CREATE units TABLE
-- ============================================================================

CREATE TABLE units (
    unit_id     UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID        NOT NULL REFERENCES properties(id),
    org_id      UUID        NOT NULL REFERENCES organizations(id),
    unit_number TEXT        NOT NULL,
    bedrooms    INT,
    bathrooms   INT,
    sq_ft       INT,
    floor       INT,
    notes       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMPTZ DEFAULT NULL
);

-- ============================================================================
-- PART 2: CREATE common_areas TABLE
-- ============================================================================

CREATE TABLE common_areas (
    common_area_id UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id    UUID        NOT NULL REFERENCES properties(id),
    org_id         UUID        NOT NULL REFERENCES organizations(id),
    name           TEXT        NOT NULL,
    type           TEXT        NOT NULL,
    description    TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMPTZ DEFAULT NULL
);

-- ============================================================================
-- PART 3: PARTIAL INDEXES FOR NON-DELETED RECORDS
-- These indexes optimize queries that filter for active (non-deleted) records
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_units_not_deleted
    ON units(deleted_at) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_common_areas_not_deleted
    ON common_areas(deleted_at) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 4: INDEXES FOR ORG-SCOPED AND PROPERTY-SCOPED QUERIES
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_units_org_id
    ON units(org_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_units_property_id
    ON units(property_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_common_areas_org_id
    ON common_areas(org_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_common_areas_property_id
    ON common_areas(property_id) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 5: ENABLE ROW LEVEL SECURITY
-- Policies are scoped by org_id to prevent cross-tenant data leakage.
-- Users may only access rows belonging to an organization they are a member of.
-- ============================================================================

ALTER TABLE units ENABLE ROW LEVEL SECURITY;
ALTER TABLE common_areas ENABLE ROW LEVEL SECURITY;

-- units policies
CREATE POLICY "org_scoped_units"
ON units
FOR ALL
TO authenticated
USING (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
);

-- common_areas policies
CREATE POLICY "org_scoped_common_areas"
ON common_areas
FOR ALL
TO authenticated
USING (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
)
WITH CHECK (
    org_id IN (
        SELECT organization_id
        FROM user_organization_mapping
        WHERE user_id = auth.uid()
    )
);

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
