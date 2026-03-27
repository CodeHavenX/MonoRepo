    -- ============================================================================
-- Migration: Create tasks table (Phase 0, DB-04)
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/439
-- Depends on: 20260309000001_create_units_and_common_areas.sql
-- ============================================================================
-- This migration:
-- 1. Creates task_status and task_priority enums
-- 2. Creates the tasks table with soft-delete support
-- 3. Adds partial indexes on deleted_at IS NULL
-- 4. Enables RLS with org-scoped policy (matching units/common_areas pattern)
-- ============================================================================

-- ============================================================================
-- PART 1: ENUMS
-- ============================================================================

CREATE TYPE task_status AS ENUM ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE task_priority AS ENUM ('LOW', 'MEDIUM', 'HIGH');

-- ============================================================================
-- PART 2: TABLE
-- ============================================================================

CREATE TABLE tasks (
    id                  UUID          DEFAULT gen_random_uuid() PRIMARY KEY,
    org_id              UUID          NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    property_id         UUID          REFERENCES properties(id) ON DELETE SET NULL,
    unit_id             UUID          REFERENCES units(unit_id) ON DELETE SET NULL,
    common_area_id      UUID          REFERENCES common_areas(common_area_id) ON DELETE SET NULL,
    assignee_id         UUID          REFERENCES users(id) ON DELETE SET NULL,
    created_by          UUID          NOT NULL REFERENCES users(id),
    status_changed_by   UUID          REFERENCES users(id) ON DELETE SET NULL,
    title               TEXT          NOT NULL,
    description         TEXT,
    priority            task_priority NOT NULL DEFAULT 'MEDIUM',
    status              task_status   NOT NULL DEFAULT 'OPEN',
    due_date            TIMESTAMPTZ,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    completed_at        TIMESTAMPTZ,
    status_changed_at   TIMESTAMPTZ,
    deleted_at          TIMESTAMPTZ   DEFAULT NULL,

    CONSTRAINT at_least_one_location CHECK (
        property_id IS NOT NULL OR unit_id IS NOT NULL OR common_area_id IS NOT NULL
    )
);

-- ============================================================================
-- PART 3: INDEXES
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_tasks_not_deleted  ON tasks(deleted_at)      WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_org_id       ON tasks(org_id)          WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_property_id  ON tasks(property_id)     WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_unit_id      ON tasks(unit_id)         WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_status       ON tasks(status)          WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_assignee_id  ON tasks(assignee_id)     WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_tasks_org_status   ON tasks(org_id, status)  WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 4: RLS — org-scoped, matching 20260309000001_create_units_and_common_areas.sql
-- ============================================================================

ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

CREATE POLICY "org_scoped_tasks"
ON tasks
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
