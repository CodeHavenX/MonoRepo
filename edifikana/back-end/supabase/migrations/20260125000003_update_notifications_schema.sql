-- Migration: Update notifications table schema
-- This migration:
-- 0. Add the recipient_email column to notifications table if it does not exist
-- 1. Makes recipient_email nullable (for notifications to existing users)
-- 2. Drops organization_id column (notifications are linked via invite_id instead)
-- 3. Adds description column for notification text
-- 4. Adds invite_id column to link notifications to invites

-- ===============================================================
-- PRELUDE: ADD recipient_email COLUMN IF NOT EXISTS
-- ===============================================================
-- Ensure recipient_email column exists before altering it.
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS recipient_email TEXT;

-- ============================================================================
-- PART 1: ALTER recipient_email TO NULLABLE
-- ============================================================================
-- recipient_email is used for notifications to users who haven't signed up yet.
-- For existing users, recipient_user_id is set and recipient_email can be null.
ALTER TABLE notifications ALTER COLUMN recipient_email DROP NOT NULL;

-- ============================================================================
-- PART 2: DROP organization_id COLUMN
-- ============================================================================
-- Organization context is now derived from the linked invite, not stored directly.
DROP INDEX IF EXISTS idx_notifications_organization;
ALTER TABLE notifications DROP COLUMN IF EXISTS organization_id;

-- ============================================================================
-- PART 3: ADD description COLUMN
-- ============================================================================
-- Description holds the notification message text.
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS description TEXT;
-- Set default for existing rows, then make NOT NULL
UPDATE notifications SET description = 'Notification' WHERE description IS NULL;
ALTER TABLE notifications ALTER COLUMN description SET NOT NULL;

-- ============================================================================
-- PART 4: ADD invite_id COLUMN
-- ============================================================================
-- Links notification to the invite that triggered it.
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS invite_id UUID REFERENCES invites(id) ON DELETE CASCADE;

-- ============================================================================
-- PART 5: UPDATE recipient_email INDEX
-- ============================================================================
-- Update the index to be a partial index (more efficient for the common query pattern)
DROP INDEX IF EXISTS idx_notifications_recipient_email;
CREATE INDEX IF NOT EXISTS idx_notifications_recipient_email
ON notifications(recipient_email)
WHERE recipient_email IS NOT NULL AND recipient_user_id IS NULL;
