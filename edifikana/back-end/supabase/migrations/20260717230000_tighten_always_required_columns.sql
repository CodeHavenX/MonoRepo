-- ============================================================================
-- Migration: Add NOT NULL to columns the application always populates
-- ============================================================================
-- Found while auditing Supabase entity type safety: these columns are
-- nullable at the DB level, but every code path that inserts a row
-- (network request validation, service-layer signatures, domain models)
-- always supplies a value. The DB schema was simply never tightened to
-- match. Verified zero existing NULL rows for each column before adding
-- the constraint.
--
-- - event_log_entries.property_id: originally added with a
--   gen_random_uuid() default (see 20241002093900_remote_schema.sql) and
--   never made NOT NULL; CreateEventLogEntryNetworkRequest.propertyId is
--   non-null and every insert path supplies it.
-- - organizations.name / organizations.description: both non-null in
--   CreateOrganizationNetworkRequest; always supplied on insert.
-- - notifications.is_read / notifications.created_at: both have DB
--   defaults (false / now()) but were never marked NOT NULL.
-- - users.first_name / users.last_name: both non-null in
--   CreateUserNetworkRequest; always supplied on insert.
-- ============================================================================

ALTER TABLE event_log_entries ALTER COLUMN property_id SET NOT NULL;

ALTER TABLE organizations ALTER COLUMN name SET NOT NULL;
ALTER TABLE organizations ALTER COLUMN description SET NOT NULL;

ALTER TABLE notifications ALTER COLUMN is_read SET NOT NULL;
ALTER TABLE notifications ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;
