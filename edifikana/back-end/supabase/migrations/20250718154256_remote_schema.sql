alter table "public"."users" add column "auth_metadata" jsonb not null default '{}'::jsonb;


