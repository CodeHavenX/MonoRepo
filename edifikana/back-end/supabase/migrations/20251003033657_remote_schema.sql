revoke delete on table "public"."staff" from "anon";

revoke insert on table "public"."staff" from "anon";

revoke references on table "public"."staff" from "anon";

revoke select on table "public"."staff" from "anon";

revoke trigger on table "public"."staff" from "anon";

revoke truncate on table "public"."staff" from "anon";

revoke update on table "public"."staff" from "anon";

revoke delete on table "public"."staff" from "authenticated";

revoke insert on table "public"."staff" from "authenticated";

revoke references on table "public"."staff" from "authenticated";

revoke select on table "public"."staff" from "authenticated";

revoke trigger on table "public"."staff" from "authenticated";

revoke truncate on table "public"."staff" from "authenticated";

revoke update on table "public"."staff" from "authenticated";

revoke delete on table "public"."staff" from "service_role";

revoke insert on table "public"."staff" from "service_role";

revoke references on table "public"."staff" from "service_role";

revoke select on table "public"."staff" from "service_role";

revoke trigger on table "public"."staff" from "service_role";

revoke truncate on table "public"."staff" from "service_role";

revoke update on table "public"."staff" from "service_role";

alter table "public"."event_log_entries" drop constraint "event_log_entries_staff_id_fkey";

alter table "public"."invites" drop constraint "invites_property_id_fkey";

alter table "public"."staff" drop constraint "staff_property_id_fkey";

alter table "public"."time_card_events" drop constraint "time_card_events_staff_id_fkey";

alter table "public"."user_organization_mapping" drop constraint "user_organization_mapping_user_id_fkey";

alter table "public"."staff" drop constraint "staff_pkey";

drop index if exists "public"."staff_pkey";

drop table "public"."staff";

create table "public"."employee" (
    "id" uuid not null default gen_random_uuid(),
    "id_type" text not null,
    "first_name" text not null,
    "last_name" text not null,
    "role" text not null,
    "property_id" uuid not null
);


alter table "public"."event_log_entries" drop column "fallback_staff_name";

alter table "public"."event_log_entries" drop column "staff_id";

alter table "public"."event_log_entries" add column "employee_id" uuid default gen_random_uuid();

alter table "public"."event_log_entries" add column "fallback_employee_name" text;

alter table "public"."invites" drop column "property_id";

alter table "public"."invites" add column "expiration" timestamp with time zone not null;

alter table "public"."invites" add column "organization_id" uuid not null default gen_random_uuid();

alter table "public"."invites" alter column "created_at" drop default;

alter table "public"."time_card_events" drop column "fallback_staff_name";

alter table "public"."time_card_events" drop column "staff_id";

alter table "public"."time_card_events" add column "employee_id" uuid default gen_random_uuid();

alter table "public"."time_card_events" add column "fallback_employee_name" text;

alter table "public"."user_organization_mapping" alter column "organization_id" set not null;

alter table "public"."user_organization_mapping" alter column "user_id" set not null;

CREATE UNIQUE INDEX staff_pkey ON public.employee USING btree (id);

alter table "public"."employee" add constraint "staff_pkey" PRIMARY KEY using index "staff_pkey";

alter table "public"."employee" add constraint "staff_property_id_fkey" FOREIGN KEY (property_id) REFERENCES properties(id) not valid;

alter table "public"."employee" validate constraint "staff_property_id_fkey";

alter table "public"."event_log_entries" add constraint "event_log_entries_employee_id_fkey" FOREIGN KEY (employee_id) REFERENCES employee(id) not valid;

alter table "public"."event_log_entries" validate constraint "event_log_entries_employee_id_fkey";

alter table "public"."invites" add constraint "invites_organization_id_fkey" FOREIGN KEY (organization_id) REFERENCES organizations(id) not valid;

alter table "public"."invites" validate constraint "invites_organization_id_fkey";

alter table "public"."time_card_events" add constraint "time_card_events_employee_id_fkey" FOREIGN KEY (employee_id) REFERENCES employee(id) not valid;

alter table "public"."time_card_events" validate constraint "time_card_events_employee_id_fkey";

grant delete on table "public"."employee" to "anon";

grant insert on table "public"."employee" to "anon";

grant references on table "public"."employee" to "anon";

grant select on table "public"."employee" to "anon";

grant trigger on table "public"."employee" to "anon";

grant truncate on table "public"."employee" to "anon";

grant update on table "public"."employee" to "anon";

grant delete on table "public"."employee" to "authenticated";

grant insert on table "public"."employee" to "authenticated";

grant references on table "public"."employee" to "authenticated";

grant select on table "public"."employee" to "authenticated";

grant trigger on table "public"."employee" to "authenticated";

grant truncate on table "public"."employee" to "authenticated";

grant update on table "public"."employee" to "authenticated";

grant delete on table "public"."employee" to "service_role";

grant insert on table "public"."employee" to "service_role";

grant references on table "public"."employee" to "service_role";

grant select on table "public"."employee" to "service_role";

grant trigger on table "public"."employee" to "service_role";

grant truncate on table "public"."employee" to "service_role";

grant update on table "public"."employee" to "service_role";


