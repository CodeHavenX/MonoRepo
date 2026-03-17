-- #418 DB-10b: Alter invites table
-- Adds invite_code, invited_by, accepted_at, unit_id columns; tightens the role column
-- with a CHECK constraint aligned to InviteRole (ADMIN, MANAGER, EMPLOYEE, RESIDENT);
-- adds a partial unique index to prevent duplicate pending invites per email+org.

-- PART 1: Add new columns (nullable initially to allow backfill before constraints)
ALTER TABLE "public"."invites"
    ADD COLUMN IF NOT EXISTS "invite_code"  TEXT         UNIQUE,
    ADD COLUMN IF NOT EXISTS "invited_by"   UUID         REFERENCES auth.users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS "accepted_at"  TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS "unit_id"      UUID         REFERENCES "public"."units"("unit_id") ON DELETE SET NULL;

-- PART 2: Backfill invite_code for existing rows that have none
UPDATE "public"."invites"
SET "invite_code" = UPPER(SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 10))
WHERE "invite_code" IS NULL;

-- PART 3: Now that every row has a code, enforce NOT NULL
ALTER TABLE "public"."invites"
    ALTER COLUMN "invite_code" SET NOT NULL;

-- PART 4: Migrate legacy role values to the new InviteRole enum values
--   'USER'      → 'EMPLOYEE'  (USER was the original default, maps to EMPLOYEE privilege)
--   All other existing values (EMPLOYEE, MANAGER, ADMIN) are already valid — no change needed
UPDATE "public"."invites"
SET "role" = 'EMPLOYEE'
WHERE "role" = 'USER';

-- PART 5: Add CHECK constraint to restrict role to valid InviteRole values
ALTER TABLE "public"."invites"
    ADD CONSTRAINT invites_role_check
    CHECK ("role" IN ('ADMIN', 'MANAGER', 'EMPLOYEE', 'RESIDENT'));

-- PART 6: Enforce that RESIDENT invites must reference a unit, and non-RESIDENT must not
ALTER TABLE "public"."invites"
    ADD CONSTRAINT invites_resident_unit_check
    CHECK (
        ("role" = 'RESIDENT' AND "unit_id" IS NOT NULL) OR
        ("role" != 'RESIDENT' AND "unit_id" IS NULL)
    );

-- PART 7: Partial unique index — only one pending (non-accepted, non-deleted) invite per email+org
CREATE UNIQUE INDEX IF NOT EXISTS invites_pending_unique_idx
    ON "public"."invites"("organization_id", "email")
    WHERE "accepted_at" IS NULL AND "deleted_at" IS NULL;

-- PART 8: Index for invite code lookups
CREATE INDEX IF NOT EXISTS idx_invites_invite_code
    ON "public"."invites"("invite_code");
