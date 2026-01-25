-- Migration: Add soft delete support to all tables
-- This migration:
-- 1. Adds deleted_at timestamp column to all tables that support deletion
-- 2. Creates partial indexes for efficient filtering of non-deleted records
-- 3. Updates views to exclude soft-deleted records

-- ============================================================================
-- PART 1: ADD deleted_at COLUMNS
-- ============================================================================

-- Add deleted_at to all tables that support deletion
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE employee ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE organizations ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE time_card_events ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE event_log_entries ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;
ALTER TABLE invites ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ DEFAULT NULL;

-- ============================================================================
-- PART 2: PARTIAL INDEXES FOR NON-DELETED RECORDS
-- These indexes optimize queries that filter for active (non-deleted) records
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_notifications_not_deleted ON notifications(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_employee_not_deleted ON employee(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_organizations_not_deleted ON organizations(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_properties_not_deleted ON properties(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_time_card_not_deleted ON time_card_events(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_event_log_not_deleted ON event_log_entries(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_not_deleted ON users(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_invites_not_deleted ON invites(deleted_at) WHERE deleted_at IS NULL;

-- ============================================================================
-- PART 3: UPDATE VIEWS TO EXCLUDE SOFT-DELETED RECORDS
-- ============================================================================

-- Update v_user_properties to exclude soft-deleted properties
CREATE OR REPLACE VIEW v_user_properties AS
SELECT
    p.id,
    p.name,
    p.address,
    p.organization_id,
    p.image_url,
    upm.user_id
FROM properties p
INNER JOIN user_property_mapping upm ON p.id = upm.property_id
WHERE p.deleted_at IS NULL;

-- Update v_user_employees to exclude soft-deleted employees
CREATE OR REPLACE VIEW v_user_employees AS
SELECT
    e.id,
    e.id_type,
    e.first_name,
    e.last_name,
    e.role,
    e.property_id,
    upm.user_id
FROM employee e
INNER JOIN user_property_mapping upm ON e.property_id = upm.property_id
WHERE e.deleted_at IS NULL;
