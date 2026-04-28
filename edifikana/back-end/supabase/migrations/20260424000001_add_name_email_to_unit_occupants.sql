-- ============================================================================
-- Migration: Add name and email columns to unit_occupants
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/419
-- ============================================================================
-- The unit_occupants table needs to capture the occupant's display name and
-- contact email so that name-only occupants (no linked user_id) are
-- representable, and so that the values shown in the UI are stable even when
-- a linked user later renames themselves. The values are written once at
-- occupant-add time and updated explicitly via the update endpoint; they are
-- not rehydrated from the users table on read.
-- ============================================================================

ALTER TABLE unit_occupants
    ADD COLUMN name  TEXT NOT NULL DEFAULT '',
    ADD COLUMN email TEXT;
