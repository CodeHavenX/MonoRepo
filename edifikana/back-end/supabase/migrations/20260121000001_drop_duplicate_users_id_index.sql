-- Drop the redundant UNIQUE constraint on users.id
-- The PRIMARY KEY constraint (users_pkey) already enforces uniqueness
-- This resolves the Supabase performance warning about identical indexes

-- First, temporarily drop foreign key constraints that depend on users(id)
ALTER TABLE "public"."user_property_mapping" DROP CONSTRAINT IF EXISTS "user_property_mapping_user_id_fkey";
ALTER TABLE "public"."global_perm_override" DROP CONSTRAINT IF EXISTS "global_perm_override_id_fkey";
ALTER TABLE "public"."user_organization_mapping" DROP CONSTRAINT IF EXISTS "user_organization_mapping_user_id_fkey";
ALTER TABLE "public"."user_organization_mapping" DROP CONSTRAINT IF EXISTS "user_organization_mapping_user_id_fkey1";
ALTER TABLE "public"."notifications" DROP CONSTRAINT IF EXISTS "notifications_recipient_user_id_fkey";

-- Drop the redundant UNIQUE constraint
ALTER TABLE ONLY "public"."users"
    DROP CONSTRAINT IF EXISTS "users_id_key";

-- Recreate the foreign key constraints (they will now reference the PRIMARY KEY)
ALTER TABLE "public"."user_property_mapping"
    ADD CONSTRAINT "user_property_mapping_user_id_fkey"
    FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE "public"."global_perm_override"
    ADD CONSTRAINT "global_perm_override_id_fkey"
    FOREIGN KEY (id) REFERENCES users(id);

ALTER TABLE "public"."user_organization_mapping"
    ADD CONSTRAINT "user_organization_mapping_user_id_fkey"
    FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE "public"."notifications"
    ADD CONSTRAINT "notifications_recipient_user_id_fkey"
    FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE CASCADE;
