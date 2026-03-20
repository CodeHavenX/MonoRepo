-- ============================================================================
-- Migration: Alter documents table — rename columns
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/389
-- Depends on: 20260318000001_create_documents_table.sql
-- ============================================================================
-- This migration:
-- 1. Renames title            → filename
-- 2. Renames file_storage_key → asset_id
-- 3. Renames uploaded_by      → created_by
-- 4. Renames uploaded_at      → created_at
-- ============================================================================
-- IDEMPOTENCY NOTE:
-- Each RENAME is wrapped in a DO block that catches `undefined_column`.
-- This is intentional: Supabase applies migrations in strict timestamp order
-- and will stop on any unhandled error. The DO blocks allow this migration to
-- be re-applied safely (e.g., after a partial failure was manually recovered)
-- without re-running the entire migration history.
--
-- IMPORTANT: Silent success does NOT mean the column exists with the new name.
-- It only means either (a) the rename succeeded, or (b) the old column was
-- already gone (renamed by a prior run). To verify the final schema state,
-- inspect the columns on the documents table directly:
--   SELECT column_name FROM information_schema.columns
--   WHERE table_name = 'documents'
--   ORDER BY ordinal_position;
--
-- Expected final columns after this migration:
--   filename, asset_id, created_by, created_at
-- ============================================================================

-- ============================================================================
-- PART 1: Rename columns
-- RENAME COLUMN is safe to re-run only if the column exists; Postgres will
-- error if the old name is not found. Wrapped in DO blocks for idempotency.
-- ============================================================================

DO $$ BEGIN
    ALTER TABLE documents RENAME COLUMN title TO filename;
EXCEPTION WHEN undefined_column THEN NULL;
END $$;

DO $$ BEGIN
    ALTER TABLE documents RENAME COLUMN file_storage_key TO asset_id;
EXCEPTION WHEN undefined_column THEN NULL;
END $$;

DO $$ BEGIN
    ALTER TABLE documents RENAME COLUMN uploaded_by TO created_by;
EXCEPTION WHEN undefined_column THEN NULL;
END $$;

DO $$ BEGIN
    ALTER TABLE documents RENAME COLUMN uploaded_at TO created_at;
EXCEPTION WHEN undefined_column THEN NULL;
END $$;

