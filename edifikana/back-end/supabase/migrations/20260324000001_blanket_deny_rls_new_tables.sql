-- ============================================================================
-- Migration: DB-11 — Blanket deny RLS on all new tables
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/394
-- ============================================================================
-- Replaces all permissive RLS policies on the following tables with the
-- blanket deny pattern: no permissive policies are defined, so all
-- non-service-role database connections are denied by default.
--
-- The backend server accesses Supabase via the service role key, which
-- bypasses RLS entirely. All authorization enforcement (org membership,
-- role gating, resident unit scoping) lives exclusively in the Kotlin BE layer
-- and is validated through unit tests.
--
-- RLS remains ENABLED on all tables — the absence of permissive policies is
-- what enforces the deny-all behavior for any connection that is not the
-- service role.
--
-- Tables affected:
--   units           — drops org_scoped_units
--   common_areas    — drops org_scoped_common_areas
--   tasks           — drops org_scoped_tasks
--   unit_occupants  — drops select/insert/update/delete_unit_occupants
--   documents       — drops org_scoped_documents
--   rent_config     — drops select/insert/update/delete_rent_config
--   payment_records — drops select/insert/update/delete_payment_records
-- ============================================================================

-- ============================================================================
-- PART 1: units
-- Policy added in: 20260309000001_create_units_and_common_areas.sql
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_units" ON units;

-- ============================================================================
-- PART 2: common_areas
-- Policy added in: 20260309000001_create_units_and_common_areas.sql
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_common_areas" ON common_areas;

-- ============================================================================
-- PART 3: tasks
-- Policy added in: 20260310000001_create_tasks_table.sql
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_tasks" ON tasks;

-- ============================================================================
-- PART 4: unit_occupants
-- Original policy (org_scoped_unit_occupants) added in:
--   20260316000001_create_unit_occupants_table.sql
-- Replaced by granular policies in:
--   20260318000002_fix_multiple_permissive_select_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "select_unit_occupants" ON unit_occupants;
DROP POLICY IF EXISTS "insert_unit_occupants" ON unit_occupants;
DROP POLICY IF EXISTS "update_unit_occupants" ON unit_occupants;
DROP POLICY IF EXISTS "delete_unit_occupants" ON unit_occupants;

-- ============================================================================
-- PART 5: documents
-- Policy added in: 20260318000001_create_documents_table.sql
-- ============================================================================

DROP POLICY IF EXISTS "org_scoped_documents" ON documents;

-- ============================================================================
-- PART 6: rent_config
-- Granular policies added in: 20260317000003_tighten_financial_rls.sql
-- SELECT policy merged in:    20260318000002_fix_multiple_permissive_select_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "select_rent_config" ON rent_config;
DROP POLICY IF EXISTS "insert_rent_config" ON rent_config;
DROP POLICY IF EXISTS "update_rent_config" ON rent_config;
DROP POLICY IF EXISTS "delete_rent_config" ON rent_config;

-- ============================================================================
-- PART 7: payment_records
-- Granular policies added in: 20260317000003_tighten_financial_rls.sql
-- SELECT policy merged in:    20260318000002_fix_multiple_permissive_select_policies.sql
-- ============================================================================

DROP POLICY IF EXISTS "select_payment_records" ON payment_records;
DROP POLICY IF EXISTS "insert_payment_records" ON payment_records;
DROP POLICY IF EXISTS "update_payment_records" ON payment_records;
DROP POLICY IF EXISTS "delete_payment_records" ON payment_records;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
