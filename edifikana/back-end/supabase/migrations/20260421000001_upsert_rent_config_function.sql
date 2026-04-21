-- ============================================================================
-- Migration: Add upsert_rent_config RPC function
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/388
-- ============================================================================
-- The Kotlin datastore previously emulated upsert with an UPDATE-then-INSERT,
-- which is susceptible to a race condition: two concurrent calls can both see
-- no active config (UPDATE returns 0 rows), then race to INSERT, causing one
-- to fail with a uniqueness violation on idx_rent_config_unit_unique.
--
-- This function resolves the race by doing the entire operation in one atomic
-- SQL statement via INSERT ... ON CONFLICT ... DO UPDATE. The partial-index
-- conflict target `(unit_id) WHERE deleted_at IS NULL` matches the unique
-- partial index created in 20260311000001, so Postgres handles the
-- insert-or-update decision at the storage level with no TOCTOU gap.
-- ============================================================================

CREATE OR REPLACE FUNCTION upsert_rent_config(
    p_unit_id         UUID,
    p_monthly_amount  NUMERIC,
    p_due_day         INT,
    p_currency        TEXT,
    p_updated_at      TIMESTAMPTZ,
    p_updated_by      UUID
)
RETURNS rent_config
LANGUAGE plpgsql
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
