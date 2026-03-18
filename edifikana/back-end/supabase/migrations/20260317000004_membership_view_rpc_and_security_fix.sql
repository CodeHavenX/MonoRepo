-- ============================================================================
-- Migration: Membership view, RPC, and security_invoker fix
-- Issue: https://github.com/CodeHavenX/MonoRepo/issues/426
-- ============================================================================
-- This migration:
-- 1. Recreates v_user_properties and v_user_employees with security_invoker=on
--    to fix Supabase lint rule 0010_security_definer_view (views in the public
--    schema default to SECURITY DEFINER, which bypasses RLS).
-- 2. Creates the new v_org_members view (with security_invoker=on) for listing
--    org members with their user profile data.
-- 3. Creates the transfer_ownership RPC for atomic ownership transfer.
-- 4. Adds granular RLS policies on user_organization_mapping for
--    INSERT/UPDATE/DELETE (OWNER-only writes).
-- ============================================================================

-- ============================================================================
-- PART 1: Fix v_user_properties — add security_invoker=on
-- ============================================================================

CREATE OR REPLACE VIEW public.v_user_properties
    WITH (security_invoker=on)
    AS SELECT p.id, p.name, p.address, p.organization_id, p.image_url, upm.user_id
       FROM properties p
       INNER JOIN user_property_mapping upm ON p.id = upm.property_id
       WHERE p.deleted_at IS NULL;

-- ============================================================================
-- PART 2: Fix v_user_employees — add security_invoker=on
-- ============================================================================

CREATE OR REPLACE VIEW public.v_user_employees
    WITH (security_invoker=on)
    AS SELECT e.id, e.id_type, e.first_name, e.last_name, e.role, e.property_id, upm.user_id
       FROM employee e
       INNER JOIN user_property_mapping upm ON e.property_id = upm.property_id
       WHERE e.deleted_at IS NULL;

-- ============================================================================
-- PART 3: Create v_org_members view (new)
-- JOINs user_organization_mapping with the users table to expose member profile
-- data (email, first_name, last_name) for the listMembers operation.
-- Only returns ACTIVE members (status = 'ACTIVE').
-- security_invoker=on ensures the caller's RLS policies are applied.
-- ============================================================================

CREATE OR REPLACE VIEW public.v_org_members
    WITH (security_invoker=on)
    AS SELECT uom.user_id,
              uom.organization_id,
              uom.role,
              uom.status,
              EXTRACT(EPOCH FROM uom.joined_at)::BIGINT AS joined_at,
              u.email,
              u.first_name,
              u.last_name
       FROM user_organization_mapping uom
       INNER JOIN users u ON uom.user_id = u.id
       WHERE uom.deleted_at IS NULL
         AND uom.status = 'ACTIVE';

GRANT SELECT ON public.v_org_members TO authenticated;
GRANT SELECT ON public.v_org_members TO service_role;

-- ============================================================================
-- PART 4: transfer_ownership RPC
-- Atomically demotes the current owner (p_caller_id) to ADMIN and promotes
-- the new owner (p_new_owner_id) to OWNER within the same transaction.
-- SECURITY DEFINER is required so the function can bypass RLS for the update.
-- The caller must be verified (OWNER role) at the service layer before calling
-- this RPC.
-- ============================================================================

CREATE OR REPLACE FUNCTION public.transfer_ownership(
    p_org_id      UUID,
    p_new_owner_id UUID,
    p_caller_id   UUID
)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    -- Demote current owner to ADMIN
    UPDATE user_organization_mapping
    SET    role = 'ADMIN'
    WHERE  organization_id = p_org_id
      AND  user_id         = p_caller_id
      AND  deleted_at      IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Caller % is not an active member of org %', p_caller_id, p_org_id;
    END IF;

    -- Promote new owner
    UPDATE user_organization_mapping
    SET    role = 'OWNER'
    WHERE  organization_id = p_org_id
      AND  user_id         = p_new_owner_id
      AND  deleted_at      IS NULL;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Target user % is not an active member of org %', p_new_owner_id, p_org_id;
    END IF;
END;
$$;

-- Only the service role (back-end server) should call this RPC.
REVOKE ALL ON FUNCTION public.transfer_ownership(UUID, UUID, UUID) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.transfer_ownership(UUID, UUID, UUID) TO service_role;

-- ============================================================================
-- PART 5: RLS policies for user_organization_mapping — INSERT/UPDATE/DELETE
-- Existing SELECT policy ("select_user_organization_mapping") is unchanged.
-- New write policies restrict mutations to OWNER-level callers only.
-- ============================================================================

-- INSERT: only OWNER of the target org may add members
CREATE POLICY "owner_insert_user_organization_mapping"
    ON user_organization_mapping
    FOR INSERT
    TO authenticated
    WITH CHECK (
        organization_id IN (
            SELECT organization_id
            FROM   user_organization_mapping
            WHERE  user_id    = auth.uid()
              AND  role       = 'OWNER'
              AND  status     = 'ACTIVE'
              AND  deleted_at IS NULL
        )
    );

-- UPDATE: only OWNER of the target org may change roles/status
CREATE POLICY "owner_update_user_organization_mapping"
    ON user_organization_mapping
    FOR UPDATE
    TO authenticated
    USING (
        organization_id IN (
            SELECT organization_id
            FROM   user_organization_mapping
            WHERE  user_id    = auth.uid()
              AND  role       = 'OWNER'
              AND  status     = 'ACTIVE'
              AND  deleted_at IS NULL
        )
    )
    WITH CHECK (
        organization_id IN (
            SELECT organization_id
            FROM   user_organization_mapping
            WHERE  user_id    = auth.uid()
              AND  role       = 'OWNER'
              AND  status     = 'ACTIVE'
              AND  deleted_at IS NULL
        )
    );

-- DELETE: only OWNER of the target org may remove membership rows
CREATE POLICY "owner_delete_user_organization_mapping"
    ON user_organization_mapping
    FOR DELETE
    TO authenticated
    USING (
        organization_id IN (
            SELECT organization_id
            FROM   user_organization_mapping
            WHERE  user_id    = auth.uid()
              AND  role       = 'OWNER'
              AND  status     = 'ACTIVE'
              AND  deleted_at IS NULL
        )
    );
