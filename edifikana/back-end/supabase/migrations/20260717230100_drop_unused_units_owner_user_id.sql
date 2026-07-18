-- ============================================================================
-- Migration: Drop unused units.owner_user_id column
-- ============================================================================
-- Added in 20260316000001_create_unit_occupants_table.sql to "track legal
-- ownership independently of physical occupancy," but never wired up in
-- application code: no field on UnitEntity, no controller/service/datastore
-- reference anywhere in the codebase. Found while auditing Supabase entity
-- type safety as schema/entity drift. Confirmed zero non-null rows before
-- dropping.
-- ============================================================================

ALTER TABLE units DROP COLUMN owner_user_id;
