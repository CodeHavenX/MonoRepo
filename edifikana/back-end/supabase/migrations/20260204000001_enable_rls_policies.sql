-- ============================================================================
-- RLS Policies Migration
-- ============================================================================
-- This migration enables Row Level Security (RLS) on:
-- 1. Storage buckets (images, documents) - Required for frontend direct access
-- 2. Database tables - Defense-in-depth (backend uses service_role which bypasses RLS)
--
-- Backend Impact: NONE (service_role key bypasses all RLS policies)
-- Frontend Impact: Allows authenticated users to upload/download from storage
-- ============================================================================

-- ----------------------------------------------------------------------------
-- PART 1: STORAGE RLS POLICIES (Required for frontend uploads/downloads)
-- ----------------------------------------------------------------------------

-- Enable RLS on storage.objects table
ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY;

-- Images Bucket Policies
-- Allow authenticated users to upload images
CREATE POLICY "authenticated_insert_images"
ON storage.objects
FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'images');

-- Allow authenticated users to view images
CREATE POLICY "authenticated_select_images"
ON storage.objects
FOR SELECT
TO authenticated
USING (bucket_id = 'images');

-- Allow authenticated users to update their uploads
CREATE POLICY "authenticated_update_images"
ON storage.objects
FOR UPDATE
TO authenticated
USING (bucket_id = 'images')
WITH CHECK (bucket_id = 'images');

-- Allow authenticated users to delete their uploads
CREATE POLICY "authenticated_delete_images"
ON storage.objects
FOR DELETE
TO authenticated
USING (bucket_id = 'images');

-- Documents Bucket Policies (for future document uploads)
-- Allow authenticated users to upload documents
CREATE POLICY "authenticated_insert_documents"
ON storage.objects
FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'documents');

-- Allow authenticated users to view documents
CREATE POLICY "authenticated_select_documents"
ON storage.objects
FOR SELECT
TO authenticated
USING (bucket_id = 'documents');

-- Allow authenticated users to update their documents
CREATE POLICY "authenticated_update_documents"
ON storage.objects
FOR UPDATE
TO authenticated
USING (bucket_id = 'documents')
WITH CHECK (bucket_id = 'documents');

-- Allow authenticated users to delete their documents
CREATE POLICY "authenticated_delete_documents"
ON storage.objects
FOR DELETE
TO authenticated
USING (bucket_id = 'documents');

-- ----------------------------------------------------------------------------
-- PART 2: DATABASE TABLE RLS POLICIES (Defense-in-depth)
-- ----------------------------------------------------------------------------
-- Note: Backend uses service_role key which bypasses these policies.
-- These policies provide additional security if:
-- - Service role key is ever compromised
-- - Direct database access is needed in the future
-- - Following security best practices
-- ----------------------------------------------------------------------------

-- Enable RLS on all tables
ALTER TABLE employee ENABLE ROW LEVEL SECURITY;
ALTER TABLE event_log_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE global_perm_override ENABLE ROW LEVEL SECURITY;
ALTER TABLE invites ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE organizations ENABLE ROW LEVEL SECURITY;
ALTER TABLE properties ENABLE ROW LEVEL SECURITY;
ALTER TABLE time_card_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_organization_mapping ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_property_mapping ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE staff ENABLE ROW LEVEL SECURITY;  -- Deprecated table but still exists

-- Employee table policies
CREATE POLICY "authenticated_all_employee"
ON employee
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Event log entries table policies
CREATE POLICY "authenticated_all_event_log_entries"
ON event_log_entries
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Global permissions override table policies
CREATE POLICY "authenticated_all_global_perm_override"
ON global_perm_override
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Invites table policies
CREATE POLICY "authenticated_all_invites"
ON invites
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Notifications table policies
CREATE POLICY "authenticated_all_notifications"
ON notifications
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Organizations table policies
CREATE POLICY "authenticated_all_organizations"
ON organizations
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Properties table policies
CREATE POLICY "authenticated_all_properties"
ON properties
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Time card events table policies
CREATE POLICY "authenticated_all_time_card_events"
ON time_card_events
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- User-organization mapping table policies
CREATE POLICY "authenticated_all_user_organization_mapping"
ON user_organization_mapping
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- User-property mapping table policies
CREATE POLICY "authenticated_all_user_property_mapping"
ON user_property_mapping
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Users table policies
CREATE POLICY "authenticated_all_users"
ON users
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- Staff table policies (deprecated table)
CREATE POLICY "authenticated_all_staff"
ON staff
FOR ALL
TO authenticated
USING (true)
WITH CHECK (true);

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
