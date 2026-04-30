-- ============================================================================
-- Migration: Harden public schema functions
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/372
-- ============================================================================
-- Fixes two Supabase security advisor warnings on public schema functions:
--
-- 1. "Public can execute SECURITY DEFINER function" (transfer_ownership)
--    REVOKE ALL ON FUNCTION ... FROM PUBLIC in 20260317000004 removed the
--    implicit default grant, but Supabase's init scripts explicitly grant
--    EXECUTE ON ALL FUNCTIONS IN SCHEMA public to anon and authenticated,
--    which overrides it. Explicit per-role revokes are required.
--    upsert_rent_config was created with no REVOKE at all.
--
-- 2. "Function has a role mutable search_path" (upsert_rent_config)
--    Without SET search_path, any caller can set their session search_path to
--    shadow public schema objects (e.g. rent_config) with their own, redirecting
--    writes. transfer_ownership already has SET search_path = public; this
--    migration brings upsert_rent_config into line.
-- ============================================================================

-- ============================================================================
-- PART 1: Revoke EXECUTE on all existing public schema functions from
-- anon and authenticated, and prevent future functions from inheriting grants.
-- ============================================================================

REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM anon;
REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM authenticated;

ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE EXECUTE ON FUNCTIONS FROM anon;
ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE EXECUTE ON FUNCTIONS FROM authenticated;

-- ============================================================================
-- PART 2: Recreate upsert_rent_config with a pinned search_path.
-- Functionally identical to the version in 20260421000001; only
-- SET search_path = public is added.
-- ============================================================================

CREATE OR REPLACE FUNCTION public.upsert_rent_config(
    p_unit_id         UUID,
    p_monthly_amount  NUMERIC,
    p_due_day         INT,
    p_currency        TEXT,
    p_updated_at      TIMESTAMPTZ,
    p_updated_by      UUID
)
RETURNS rent_config
LANGUAGE plpgsql
SECURITY INVOKER
SET search_path = public
AS $$
DECLARE
    v_result rent_config;
BEGIN
    INSERT INTO rent_config (unit_id, monthly_amount, due_day, currency, updated_at, updated_by)
    VALUES (p_unit_id, p_monthly_amount, p_due_day, p_currency, p_updated_at, p_updated_by)
    ON CONFLICT (unit_id) WHERE deleted_at IS NULL
    DO UPDATE SET
        monthly_amount = EXCLUDED.monthly_amount,
        due_day        = EXCLUDED.due_day,
        currency       = EXCLUDED.currency,
        updated_at     = EXCLUDED.updated_at,
        updated_by     = EXCLUDED.updated_by
    RETURNING * INTO v_result;

    RETURN v_result;
END;
$$;

-- ============================================================================
-- PART 3: Re-grant EXECUTE to service_role for both RPCs.
-- PART 1 revoked from anon/authenticated only, so service_role is unaffected,
-- but explicit grants make the intent clear and are resilient to future
-- Supabase init script changes.
-- ============================================================================

GRANT EXECUTE ON FUNCTION public.transfer_ownership(UUID, UUID, UUID) TO service_role;
GRANT EXECUTE ON FUNCTION public.upsert_rent_config(UUID, NUMERIC, INT, TEXT, TIMESTAMPTZ, UUID) TO service_role;
