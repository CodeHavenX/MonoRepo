-- Optimization indexes and constraints
-- This migration adds indexes for frequently queried columns and unique constraints on mapping tables

-- ============================================================================
-- NOTIFICATION INDEXES
-- ============================================================================

-- Notification queries: user + read status filter
CREATE INDEX IF NOT EXISTS idx_notifications_user_read
ON notifications(recipient_user_id, is_read);

-- ============================================================================
-- ADD invite_id COLUMN
-- ============================================================================
-- Links notification to the invite that triggered it.
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS invite_id UUID REFERENCES invites(id) ON DELETE CASCADE;

-- Notification queries by invite_id
CREATE INDEX IF NOT EXISTS idx_notifications_invite
ON notifications(invite_id);

-- ============================================================================
-- INVITE INDEXES
-- ============================================================================

-- Invite queries: organization + expiration filter
CREATE INDEX IF NOT EXISTS idx_invites_org_expiration
ON invites(organization_id, expiration);

-- Invite queries by email + expiration (for checking user's pending invites)
CREATE INDEX IF NOT EXISTS idx_invites_email_expiration
ON invites(email, expiration);

-- ============================================================================
-- MAPPING TABLE INDEXES
-- ============================================================================

-- User-property permission checks: user_id with property_id
CREATE INDEX IF NOT EXISTS idx_user_property_user
ON user_property_mapping(user_id, property_id);

-- User-property by property (for finding all users with access to a property)
CREATE INDEX IF NOT EXISTS idx_user_property_property
ON user_property_mapping(property_id);

-- User-organization role lookups: user_id with role
CREATE INDEX IF NOT EXISTS idx_user_org_user_role
ON user_organization_mapping(user_id, role);

-- User-organization by organization (for finding all users in an organization)
CREATE INDEX IF NOT EXISTS idx_user_org_organization
ON user_organization_mapping(organization_id);

-- ============================================================================
-- TIME CARD AND EVENT LOG INDEXES
-- ============================================================================

-- Time card events by employee and timestamp (common query pattern)
CREATE INDEX IF NOT EXISTS idx_time_card_employee_timestamp
ON time_card_events(employee_id, timestamp DESC);

-- Time card events by property (for property-wide reports)
CREATE INDEX IF NOT EXISTS idx_time_card_property
ON time_card_events(property_id);

-- Event log entries by property and timestamp (common query pattern)
CREATE INDEX IF NOT EXISTS idx_event_log_property_timestamp
ON event_log_entries(property_id, timestamp DESC);

-- Event log entries by employee (for employee activity history)
CREATE INDEX IF NOT EXISTS idx_event_log_employee
ON event_log_entries(employee_id);

-- ============================================================================
-- EMPLOYEE INDEXES
-- ============================================================================

-- Employee by property (for listing employees per property)
CREATE INDEX IF NOT EXISTS idx_employee_property
ON employee(property_id);

-- ============================================================================
-- USER INDEXES
-- ============================================================================

-- User lookup by email (common pattern, should be unique)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email
ON users(email);

-- Unique constraint on user_property_mapping to prevent duplicate entries
-- Using IF NOT EXISTS pattern with DO block for idempotency
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'unique_user_property'
        AND conrelid = 'user_property_mapping'::regclass
    ) THEN
        ALTER TABLE user_property_mapping
        ADD CONSTRAINT unique_user_property UNIQUE (user_id, property_id);
    END IF;
END $$;

-- Unique constraint on user_organization_mapping to prevent duplicate entries
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'unique_user_organization'
        AND conrelid = 'user_organization_mapping'::regclass
    ) THEN
        ALTER TABLE user_organization_mapping
        ADD CONSTRAINT unique_user_organization UNIQUE (user_id, organization_id);
    END IF;
END $$;
