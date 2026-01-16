-- Add name and description columns to organizations table
alter table "public"."organizations" add column "name" text;
alter table "public"."organizations" add column "description" text;
