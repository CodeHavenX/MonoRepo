alter table "public"."users" add column "address" jsonb;

alter table "public"."users" add column "emails" text[];

alter table "public"."users" add column "first_name" text;

alter table "public"."users" add column "is_verified" boolean not null default false;

alter table "public"."users" add column "last_name" text;

alter table "public"."users" add column "phone_numbers" text[];


