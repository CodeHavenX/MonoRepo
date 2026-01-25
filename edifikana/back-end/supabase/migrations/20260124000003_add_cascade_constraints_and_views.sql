-- Migration: Add CASCADE constraints and database views for query optimization
-- This migration:
-- 1. Adds ON DELETE CASCADE to mapping table foreign keys for automatic cleanup
-- 2. Creates views to eliminate N+1 query patterns

-- ============================================================================
-- PART 1: CASCADE CONSTRAINTS
-- ============================================================================

-- Drop and recreate user_property_mapping FK with CASCADE
-- This allows deleting a property to automatically remove all user-property mappings
ALTER TABLE user_property_mapping
DROP CONSTRAINT IF EXISTS user_property_mapping_property_id_fkey;

ALTER TABLE user_property_mapping
ADD CONSTRAINT user_property_mapping_property_id_fkey
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE;

-- Drop and recreate user_organization_mapping FK with CASCADE
-- This allows deleting an organization to automatically remove all user-organization mappings
ALTER TABLE user_organization_mapping
DROP CONSTRAINT IF EXISTS user_organization_mapping_organization_id_fkey;

ALTER TABLE user_organization_mapping
ADD CONSTRAINT user_organization_mapping_organization_id_fkey
    FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE CASCADE;

-- Add CASCADE to user_property_mapping user FK (cleanup when user is deleted)
ALTER TABLE user_property_mapping
DROP CONSTRAINT IF EXISTS user_property_mapping_user_id_fkey;

ALTER TABLE user_property_mapping
ADD CONSTRAINT user_property_mapping_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add CASCADE to user_organization_mapping user FK (cleanup when user is deleted)
-- Note: There may be duplicate constraints, clean them up
ALTER TABLE user_organization_mapping
DROP CONSTRAINT IF EXISTS user_organization_mapping_user_id_fkey;
ALTER TABLE user_organization_mapping
DROP CONSTRAINT IF EXISTS user_organization_mapping_user_id_fkey1;

ALTER TABLE user_organization_mapping
ADD CONSTRAINT user_organization_mapping_user_id_fkey
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- ============================================================================
-- PART 2: DATABASE VIEWS
-- ============================================================================

-- View: v_user_properties
-- Provides direct access to properties for a given user through the mapping table
-- Eliminates N+1 query pattern in getProperties()
CREATE OR REPLACE VIEW v_user_properties AS
SELECT
    p.id,
    p.name,
    p.address,
    p.organization_id,
    p.image_url,
    upm.user_id
FROM properties p
INNER JOIN user_property_mapping upm ON p.id = upm.property_id;

-- View: v_user_employees
-- Provides direct access to employees for a given user through property mappings
-- Eliminates N+1 query pattern in getEmployees()
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
INNER JOIN user_property_mapping upm ON e.property_id = upm.property_id;

-- ============================================================================
-- PART 3: GRANT PERMISSIONS ON VIEWS
-- ============================================================================

GRANT SELECT ON v_user_properties TO authenticated;
GRANT SELECT ON v_user_properties TO service_role;
GRANT SELECT ON v_user_properties TO anon;

GRANT SELECT ON v_user_employees TO authenticated;
GRANT SELECT ON v_user_employees TO service_role;
GRANT SELECT ON v_user_employees TO anon;
