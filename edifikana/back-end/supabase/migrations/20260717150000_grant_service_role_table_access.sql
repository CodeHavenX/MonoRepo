-- ============================================================================
-- Migration: Grant service_role full access to public schema tables
-- ============================================================================
-- Tables introduced via hand-written migrations (users, properties, tasks,
-- units, rent_config, payment_records, occupants, etc.) never received
-- explicit GRANTs to service_role, unlike tables generated via Supabase's
-- schema-diff tooling (invites, organizations, employee, user_organization_
-- mapping, global_perm_override, staff), which have per-table GRANT
-- statements baked into their migrations.
--
-- This went unnoticed because every environment that has run this migration
-- set so far (hosted Supabase projects, and any long-lived local dev
-- database) was originally bootstrapped by Supabase's control plane, which
-- separately establishes correct default privileges for the `postgres` role
-- outside of this migrations folder. A database built purely by replaying
-- these migrations from an empty Postgres instance does not get that
-- bootstrap, and ends up with service_role missing SELECT/INSERT/UPDATE on
-- every affected table (only DELETE/REFERENCES/TRIGGER are granted by
-- Postgres's own bare default).
--
-- service_role bypasses Row Level Security (BYPASSRLS), but RLS bypass is
-- independent of object-level GRANTs -- a role still needs GRANTs to touch a
-- table at all, regardless of RLS.
-- ============================================================================

-- ============================================================================
-- PART 1: Catch-up grant for all existing public schema tables.
-- ============================================================================

GRANT ALL ON ALL TABLES IN SCHEMA public TO service_role;

-- ============================================================================
-- PART 2: Fix default privileges so tables created by future migrations
-- (which all run as the `postgres` role) automatically grant service_role
-- full access, without needing a per-table GRANT statement.
-- ============================================================================

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES TO service_role;
