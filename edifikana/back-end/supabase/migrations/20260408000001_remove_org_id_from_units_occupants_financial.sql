-- ============================================================================
-- Migration: Remove org_id from units, unit_occupants, rent_config, payment_records
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/463
-- ============================================================================
-- org_id is redundant on these tables: org membership is always derivable via
-- property_id → properties → organizations (for units), or via unit_id → units
-- → properties → organizations (for the others). Authorization is enforced
-- exclusively in the Kotlin BE layer (service role bypasses RLS).
--
-- common_areas.org_id was already removed in 20260327000001.
-- tasks.org_id was already removed in 20260407000001.
--
-- Migration order is critical:
--   1. Drop composite FKs on unit_occupants/rent_config/payment_records first
--      (they reference units(unit_id, org_id))
--   2. Then drop the units_unit_id_org_id_unique composite constraint
--   3. Then drop org_id columns (units last — it was the FK target)
-- ============================================================================

-- ============================================================================
-- STEP 1: Drop composite FKs that reference units(unit_id, org_id)
-- ============================================================================

ALTER TABLE unit_occupants  DROP CONSTRAINT IF EXISTS unit_occupants_unit_org_fk;
ALTER TABLE rent_config     DROP CONSTRAINT IF EXISTS rent_config_unit_org_fk;
ALTER TABLE payment_records DROP CONSTRAINT IF EXISTS payment_records_unit_org_fk;

-- ============================================================================
-- STEP 2: Add simple FKs to units(unit_id) with ON DELETE CASCADE
-- ============================================================================

ALTER TABLE unit_occupants  ADD CONSTRAINT unit_occupants_unit_id_fkey
    FOREIGN KEY (unit_id) REFERENCES units(unit_id) ON DELETE CASCADE;

ALTER TABLE rent_config     ADD CONSTRAINT rent_config_unit_id_fkey
    FOREIGN KEY (unit_id) REFERENCES units(unit_id) ON DELETE CASCADE;

ALTER TABLE payment_records ADD CONSTRAINT payment_records_unit_id_fkey
    FOREIGN KEY (unit_id) REFERENCES units(unit_id) ON DELETE CASCADE;

-- ============================================================================
-- STEP 3: Drop the active-occupancy-per-user uniqueness index
-- The old index constrained one ACTIVE occupancy per (user_id, org_id).
-- Without org_id, a global (user_id) constraint would incorrectly block a
-- user from having active occupancies in multiple units across different
-- properties or orgs. There is no meaningful uniqueness to enforce here:
-- a user may legitimately occupy units in multiple properties and orgs.
-- The per-(unit_id, user_id) index (idx_unit_occupants_active_unit_user)
-- already prevents duplicate active records for the same unit+user pair.
-- ============================================================================

DROP INDEX IF EXISTS idx_unit_occupants_one_active_per_user;
-- ============================================================================
-- STEP 4: Drop the composite unique constraint on units
-- This constraint existed solely to support composite FK references from
-- unit_occupants, rent_config, and payment_records (all dropped above).
-- ============================================================================

ALTER TABLE units DROP CONSTRAINT IF EXISTS units_unit_id_org_id_unique;

-- ============================================================================
-- STEP 5: Drop org_id-scoped indexes
-- ============================================================================

DROP INDEX IF EXISTS idx_units_org_id;
DROP INDEX IF EXISTS idx_unit_occupants_org_id;
DROP INDEX IF EXISTS idx_rent_config_org_id;
DROP INDEX IF EXISTS idx_payment_records_org_id;

-- ============================================================================
-- STEP 6: Drop remaining org-scoped RLS policies
-- Most were already removed by 20260324000001_blanket_deny_rls_new_tables.sql.
-- IF EXISTS guards handle whichever state the DB is in.
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_units"           ON units;
DROP POLICY IF EXISTS "org_scoped_unit_occupants"  ON unit_occupants;
DROP POLICY IF EXISTS "org_scoped_rent_config"     ON rent_config;
DROP POLICY IF EXISTS "org_scoped_payment_records" ON payment_records;

-- ============================================================================
-- STEP 7: Drop org_id columns
-- units is dropped LAST because it was the FK target for the others.
-- ============================================================================

ALTER TABLE unit_occupants  DROP COLUMN IF EXISTS org_id;
ALTER TABLE rent_config     DROP COLUMN IF EXISTS org_id;
ALTER TABLE payment_records DROP COLUMN IF EXISTS org_id;
ALTER TABLE units           DROP COLUMN IF EXISTS org_id;

-- ============================================================================
-- STEP 8: Add missing composite index on tasks
-- This index was specified in the tasks schema design but was not included
-- in the 20260407000001 migration. It supports the primary list query
-- (tasks by property, filtered by status).
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_tasks_property_status
    ON tasks(property_id, status) WHERE deleted_at IS NULL;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
