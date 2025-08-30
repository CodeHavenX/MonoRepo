create table "public"."invites" (
    "id" uuid not null default gen_random_uuid(),
    "created_at" timestamp with time zone not null default now(),
    "property_id" uuid not null default gen_random_uuid(),
    "email" text not null
);


create table "public"."organizations" (
    "id" uuid not null default gen_random_uuid(),
    "created_at" timestamp with time zone not null default now()
);


create table "public"."user_organization_mapping" (
    "id" uuid not null default gen_random_uuid(),
    "created_at" timestamp with time zone not null default now(),
    "user_id" uuid,
    "organization_id" uuid
);


alter table "public"."user_organization_mapping" enable row level security;

alter table "public"."properties" add column "organization_id" uuid not null;

alter table "public"."properties" alter column "address" set not null;

CREATE UNIQUE INDEX invites_pkey ON public.invites USING btree (id);

CREATE UNIQUE INDEX organizations_pkey ON public.organizations USING btree (id);

CREATE UNIQUE INDEX user_organization_mapping_pkey ON public.user_organization_mapping USING btree (id);

alter table "public"."invites" add constraint "invites_pkey" PRIMARY KEY using index "invites_pkey";

alter table "public"."organizations" add constraint "organizations_pkey" PRIMARY KEY using index "organizations_pkey";

alter table "public"."user_organization_mapping" add constraint "user_organization_mapping_pkey" PRIMARY KEY using index "user_organization_mapping_pkey";

alter table "public"."invites" add constraint "invites_property_id_fkey" FOREIGN KEY (property_id) REFERENCES properties(id) not valid;

alter table "public"."invites" validate constraint "invites_property_id_fkey";

alter table "public"."properties" add constraint "properties_organization_id_fkey" FOREIGN KEY (organization_id) REFERENCES organizations(id) not valid;

alter table "public"."properties" validate constraint "properties_organization_id_fkey";

alter table "public"."user_organization_mapping" add constraint "user_organization_mapping_organization_id_fkey" FOREIGN KEY (organization_id) REFERENCES organizations(id) not valid;

alter table "public"."user_organization_mapping" validate constraint "user_organization_mapping_organization_id_fkey";

alter table "public"."user_organization_mapping" add constraint "user_organization_mapping_user_id_fkey" FOREIGN KEY (user_id) REFERENCES users(id) not valid;

alter table "public"."user_organization_mapping" validate constraint "user_organization_mapping_user_id_fkey";

alter table "public"."user_organization_mapping" add constraint "user_organization_mapping_user_id_fkey1" FOREIGN KEY (user_id) REFERENCES users(id) not valid;

alter table "public"."user_organization_mapping" validate constraint "user_organization_mapping_user_id_fkey1";

grant delete on table "public"."invites" to "anon";

grant insert on table "public"."invites" to "anon";

grant references on table "public"."invites" to "anon";

grant select on table "public"."invites" to "anon";

grant trigger on table "public"."invites" to "anon";

grant truncate on table "public"."invites" to "anon";

grant update on table "public"."invites" to "anon";

grant delete on table "public"."invites" to "authenticated";

grant insert on table "public"."invites" to "authenticated";

grant references on table "public"."invites" to "authenticated";

grant select on table "public"."invites" to "authenticated";

grant trigger on table "public"."invites" to "authenticated";

grant truncate on table "public"."invites" to "authenticated";

grant update on table "public"."invites" to "authenticated";

grant delete on table "public"."invites" to "service_role";

grant insert on table "public"."invites" to "service_role";

grant references on table "public"."invites" to "service_role";

grant select on table "public"."invites" to "service_role";

grant trigger on table "public"."invites" to "service_role";

grant truncate on table "public"."invites" to "service_role";

grant update on table "public"."invites" to "service_role";

grant delete on table "public"."organizations" to "anon";

grant insert on table "public"."organizations" to "anon";

grant references on table "public"."organizations" to "anon";

grant select on table "public"."organizations" to "anon";

grant trigger on table "public"."organizations" to "anon";

grant truncate on table "public"."organizations" to "anon";

grant update on table "public"."organizations" to "anon";

grant delete on table "public"."organizations" to "authenticated";

grant insert on table "public"."organizations" to "authenticated";

grant references on table "public"."organizations" to "authenticated";

grant select on table "public"."organizations" to "authenticated";

grant trigger on table "public"."organizations" to "authenticated";

grant truncate on table "public"."organizations" to "authenticated";

grant update on table "public"."organizations" to "authenticated";

grant delete on table "public"."organizations" to "service_role";

grant insert on table "public"."organizations" to "service_role";

grant references on table "public"."organizations" to "service_role";

grant select on table "public"."organizations" to "service_role";

grant trigger on table "public"."organizations" to "service_role";

grant truncate on table "public"."organizations" to "service_role";

grant update on table "public"."organizations" to "service_role";

grant delete on table "public"."user_organization_mapping" to "anon";

grant insert on table "public"."user_organization_mapping" to "anon";

grant references on table "public"."user_organization_mapping" to "anon";

grant select on table "public"."user_organization_mapping" to "anon";

grant trigger on table "public"."user_organization_mapping" to "anon";

grant truncate on table "public"."user_organization_mapping" to "anon";

grant update on table "public"."user_organization_mapping" to "anon";

grant delete on table "public"."user_organization_mapping" to "authenticated";

grant insert on table "public"."user_organization_mapping" to "authenticated";

grant references on table "public"."user_organization_mapping" to "authenticated";

grant select on table "public"."user_organization_mapping" to "authenticated";

grant trigger on table "public"."user_organization_mapping" to "authenticated";

grant truncate on table "public"."user_organization_mapping" to "authenticated";

grant update on table "public"."user_organization_mapping" to "authenticated";

grant delete on table "public"."user_organization_mapping" to "service_role";

grant insert on table "public"."user_organization_mapping" to "service_role";

grant references on table "public"."user_organization_mapping" to "service_role";

grant select on table "public"."user_organization_mapping" to "service_role";

grant trigger on table "public"."user_organization_mapping" to "service_role";

grant truncate on table "public"."user_organization_mapping" to "service_role";

grant update on table "public"."user_organization_mapping" to "service_role";


