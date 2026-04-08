-- ============================================================================
-- Migration: Change tasks.due_date from TIMESTAMPTZ to DATE
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/387
-- ============================================================================
-- due_date represents a calendar date with no time component.
-- DATE is the correct PostgreSQL type and aligns with kotlinx.datetime.LocalDate
-- on the Kotlin side. No indexes or views reference this column.
-- ============================================================================

ALTER TABLE tasks
    ALTER COLUMN due_date TYPE DATE
    USING due_date::DATE;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
