-- ============================================================================
-- Migration: Convert bigint timestamp columns to TIMESTAMPTZ
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/387
-- ============================================================================
-- The original event_log_entries and time_card_events tables stored their
-- "timestamp" column as epoch-seconds bigint. The Kotlin entities model these
-- as kotlin.time.Instant, which the Supabase SDK maps to TIMESTAMPTZ.
-- This migration converts those columns and updates the v_org_members view to
-- return joined_at as TIMESTAMPTZ instead of a bigint epoch-seconds value.
-- ============================================================================

-- event_log_entries.timestamp: bigint (epoch seconds) → TIMESTAMPTZ
ALTER TABLE event_log_entries
    ALTER COLUMN "timestamp" TYPE TIMESTAMPTZ
    USING to_timestamp("timestamp");

-- time_card_events.timestamp: bigint (epoch seconds) → TIMESTAMPTZ
ALTER TABLE time_card_events
    ALTER COLUMN "timestamp" TYPE TIMESTAMPTZ
    USING to_timestamp("timestamp");

-- Recreate v_org_members to expose joined_at as TIMESTAMPTZ instead of BIGINT.
-- The prior version used EXTRACT(EPOCH FROM joined_at)::BIGINT which produced
-- a numeric value the Kotlin Instant deserializer cannot parse.
-- DROP required because CREATE OR REPLACE VIEW cannot change a column's type.
DROP VIEW IF EXISTS public.v_org_members;
CREATE VIEW public.v_org_members
    WITH (security_invoker=on)
    AS SELECT uom.user_id,
              uom.organization_id,
              uom.role,
              uom.status,
              uom.joined_at,
              u.email,
              u.first_name,
              u.last_name
       FROM user_organization_mapping uom
       INNER JOIN users u ON uom.user_id = u.id
       WHERE uom.status = 'ACTIVE'
         AND u.deleted_at IS NULL;

GRANT SELECT ON public.v_org_members TO service_role;
