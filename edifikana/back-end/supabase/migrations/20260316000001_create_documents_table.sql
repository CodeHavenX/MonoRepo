-- ============================================================================
-- Migration: Create documents table
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/384
-- Depends on: 20260309000001_create_units_and_common_areas.sql
--             20260310000001_create_tasks_table.sql (no direct dep, same sprint)
-- ============================================================================
-- This migration:
-- 1. Creates the documents table with org, property, and unit scoping
-- 2. Adds partial indexes (soft-delete aware)
-- 3. Enables RLS with an org-scoped policy using (SELECT auth.uid()) for performance
-- ============================================================================
-- NOTE: document_type is stored as TEXT rather than a Postgres ENUM to allow
-- future values to be added without a schema migration. Valid values are:
--   LEASE | ID_COPY | INSPECTION | NOTICE | OTHER
-- Enforcement of valid values is handled at the application layer.
-- ============================================================================
-- NOTE: Resident access restriction (limiting reads to documents scoped to the
-- resident's own unit) is enforced at the RBAC layer in the back-end service,
-- not at the SQL layer. The RLS policy below grants access to all authenticated
-- members of the organization. This is a deliberate architectural decision —
-- the application layer is responsible for role-based filtering.
-- ============================================================================

-- ============================================================================
-- PART 1: documents TABLE
-- ============================================================================

CREATE TABLE documents (
    document_id      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id           UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    property_id      UUID        REFERENCES properties(id) ON DELETE CASCADE,
    unit_id          UUID        REFERENCES units(unit_id) ON DELETE CASCADE,
    title            TEXT        NOT NULL,
    file_storage_key TEXT        NOT NULL,
    mime_type        TEXT        NOT NULL,
    document_type    TEXT        NOT NULL,
    uploaded_by      UUID        REFERENCES users(id) ON DELETE SET NULL,
    uploaded_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at       TIMESTAMPTZ DEFAULT NULL
);

-- ============================================================================
-- PART 2: INDEXES
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_documents_org_id
    ON documents(org_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_documents_property_id
    ON documents(property_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_documents_unit_id
    ON documents(unit_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_documents_not_deleted
    ON documents(deleted_at) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 3: RLS — org-scoped, using (SELECT auth.uid()) per Supabase perf docs
-- See: https://supabase.com/docs/guides/database/postgres/row-level-security#call-functions-with-select
-- ============================================================================

ALTER TABLE documents ENABLE ROW LEVEL SECURITY;

CREATE POLICY "org_scoped_documents"
ON documents
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
