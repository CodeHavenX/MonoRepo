-- #418 DB-10b: Alter invites table
-- Adds invite_code, invited_by, accepted_at, unit_id columns; tightens the role column
-- with a CHECK constraint aligned to InviteRole (ADMIN, MANAGER, EMPLOYEE, RESIDENT);
-- adds a partial unique index to prevent duplicate pending invites per email+org.

-- PART 1: Add new columns (nullable, no uniqueness constraint yet — added after backfill)
ALTER TABLE "public"."invites"
    ADD COLUMN IF NOT EXISTS "invite_code"  TEXT,
    ADD COLUMN IF NOT EXISTS "invited_by"   UUID         REFERENCES auth.users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS "accepted_at"  TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS "unit_id"      UUID         REFERENCES "public"."units"("unit_id") ON DELETE SET NULL;

-- PART 2: Backfill invite_code for existing rows that have none.
-- Uses gen_random_uuid() (built into Postgres 13+, no extension required) as the
-- entropy source — UUID-quality randomness avoids the collision risk of MD5(RANDOM()).
-- 10 hex chars from a UUID = 40 bits of entropy per code.
UPDATE "public"."invites"
SET "invite_code" = UPPER(LEFT(REPLACE(GEN_RANDOM_UUID()::TEXT, '-', ''), 10))
WHERE "invite_code" IS NULL;

-- PART 3: Now that every row has a code, enforce NOT NULL
ALTER TABLE "public"."invites"
    ALTER COLUMN "invite_code" SET NOT NULL;

-- PART 4: Add UNIQUE constraint after backfill to avoid mid-backfill collision failures.
-- If any two rows had received the same code above, the migration would have failed here
-- rather than mid-UPDATE, giving a clear error at the right step.
ALTER TABLE "public"."invites"
    ADD CONSTRAINT invites_invite_code_unique UNIQUE ("invite_code");

-- PART 5: Migrate legacy role values to the new InviteRole enum values
--   'USER'      → 'EMPLOYEE'  (USER was the original default, maps to EMPLOYEE privilege)
--   All other existing values (EMPLOYEE, MANAGER, ADMIN) are already valid — no change needed
UPDATE "public"."invites"
SET "role" = 'EMPLOYEE'
WHERE "role" = 'USER';

-- PART 6: Add CHECK constraint to restrict role to valid InviteRole values
ALTER TABLE "public"."invites"
    ADD CONSTRAINT invites_role_check
    CHECK ("role" IN ('ADMIN', 'MANAGER', 'EMPLOYEE', 'RESIDENT'));

-- PART 7: Enforce that RESIDENT invites must reference a unit, and non-RESIDENT must not
ALTER TABLE "public"."invites"
    ADD CONSTRAINT invites_resident_unit_check
    CHECK (
        ("role" = 'RESIDENT' AND "unit_id" IS NOT NULL) OR
        ("role" != 'RESIDENT' AND "unit_id" IS NULL)
    );

-- PART 8: Deduplicate pending invites before creating the unique index.
-- Prior schema versions allowed multiple pending invites for the same (organization_id, email).
-- For each such pair, soft-delete all but the newest row (by created_at) so the index
-- creation below does not fail on pre-existing duplicates.
WITH newest_pending AS (
    SELECT DISTINCT ON (organization_id, email) id
    FROM "public"."invites"
    WHERE accepted_at IS NULL AND deleted_at IS NULL
    ORDER BY organization_id, email, created_at DESC
)
UPDATE "public"."invites"
SET deleted_at = NOW()
WHERE accepted_at IS NULL
  AND deleted_at IS NULL
  AND id NOT IN (SELECT id FROM newest_pending);

-- PART 9: Partial unique index — only one pending (non-accepted, non-deleted) invite per email+org
-- Safe to create now that duplicates have been soft-deleted in Part 8.
CREATE UNIQUE INDEX IF NOT EXISTS invites_pending_unique_idx
    ON "public"."invites"("organization_id", "email")
    WHERE "accepted_at" IS NULL AND "deleted_at" IS NULL;
