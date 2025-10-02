alter table "public"."invites" drop constraint "invites_property_id_fkey";

alter table "public"."user_organization_mapping" drop constraint "user_organization_mapping_user_id_fkey";

alter table "public"."invites" drop column "property_id";

alter table "public"."invites" add column "expiration" timestamp with time zone not null;

alter table "public"."invites" add column "organization_id" uuid not null default gen_random_uuid();

alter table "public"."invites" alter column "created_at" drop default;

alter table "public"."user_organization_mapping" alter column "organization_id" set not null;

alter table "public"."user_organization_mapping" alter column "user_id" set not null;

alter table "public"."invites" add constraint "invites_organization_id_fkey" FOREIGN KEY (organization_id) REFERENCES organizations(id) not valid;

alter table "public"."invites" validate constraint "invites_organization_id_fkey";


