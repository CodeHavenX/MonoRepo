-- ============================================================================
-- Migration: Alter user_organization_mapping
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/418
-- ============================================================================
-- This migration:
-- 1. Creates the org_member_status enum (ACTIVE, INACTIVE, PENDING)
-- 2. Adds status, invited_by, and joined_at columns
-- 3. Backfills status for existing rows
-- 4. Backfills legacy role values to conform to the new constraint
-- 5. Adds a CHECK constraint limiting role to OWNER, ADMIN, MANAGER, EMPLOYEE
-- 6. Adds an index on (organization_id, status) for efficient status queries
-- ============================================================================
-- NOTE ON ROLE VALUES:
--   The existing TEXT column stores legacy values such as 'USER', 'MANAGER', etc.
--   The new CHECK constraint restricts the column to ('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE').
--   MANAGER and EMPLOYEE rows already conform and are not backfilled.
--   USER and SUPERUSER are application-only bootstrapping values that should not
--   persist in the DB; they are converted to 'ADMIN' as a safe default since
--   all pre-migration rows represent admin-level org creators.
-- ============================================================================

-- ============================================================================
-- PART 1: Create org_member_status enum
-- ============================================================================

CREATE TYPE org_member_status AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING');

-- ============================================================================
-- PART 2: Add new columns
-- ADD COLUMN IF NOT EXISTS makes this migration idempotent (safe to re-run).
-- ============================================================================

ALTER TABLE user_organization_mapping
    ADD COLUMN IF NOT EXISTS status     org_member_status NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS invited_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS joined_at  TIMESTAMPTZ;

-- ============================================================================
-- PART 3: Backfill status for existing rows
-- All pre-existing rows are active members; no PENDING rows can pre-exist.
-- The DEFAULT 'ACTIVE' handles new rows, but NULL rows from pre-migration
-- state (if any) are covered by this explicit UPDATE.
-- ============================================================================

UPDATE user_organization_mapping
SET status = 'ACTIVE'
WHERE status IS NULL;

-- ============================================================================
-- PART 4: Backfill legacy role values
-- USER and SUPERUSER are bootstrapping-only values that never belong in the DB.
-- All such rows represent the org creator, which maps to Admin level.
-- MANAGER and EMPLOYEE rows are already valid and are not touched.
-- ============================================================================

UPDATE user_organization_mapping
SET role = 'ADMIN'
WHERE role IN ('USER', 'SUPERUSER');

-- ============================================================================
-- PART 5: Add CHECK constraint to enforce valid role values going forward
-- ============================================================================

ALTER TABLE user_organization_mapping
    ADD CONSTRAINT user_org_mapping_role_check
    CHECK (role IN ('OWNER', 'ADMIN', 'MANAGER', 'EMPLOYEE'));

-- ============================================================================
-- PART 6: Index for efficient org-scoped status queries
-- Supports lookups like "list all ACTIVE members of an org"
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_user_org_mapping_org_status
    ON user_organization_mapping (organization_id, status);
