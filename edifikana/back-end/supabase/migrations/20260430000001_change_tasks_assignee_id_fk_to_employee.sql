-- ============================================================================
-- Migration: Change tasks.assignee_id FK from users to employee
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/475
-- ============================================================================
-- Task.assigneeId changed from UserId? to EmployeeId? in #475.
-- The column must now reference employee(id) rather than users(id).
--
-- Existing non-null assignee_id values are cleared because they store user UUIDs
-- which will not exist in the employee table and would violate the new FK.
-- ============================================================================

-- Clear existing assignee_id values — they reference users, not employees
UPDATE tasks SET assignee_id = NULL WHERE assignee_id IS NOT NULL;

-- Drop the old FK constraint that referenced users(id)
ALTER TABLE tasks DROP CONSTRAINT IF EXISTS tasks_assignee_id_fkey;

-- Add new FK constraint referencing employee(id)
ALTER TABLE tasks ADD CONSTRAINT tasks_assignee_id_fkey
  FOREIGN KEY (assignee_id) REFERENCES employee(id) ON DELETE SET NULL;

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================
