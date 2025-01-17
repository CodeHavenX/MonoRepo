create table "public"."global_perm_override" (
    "id" uuid not null
);


CREATE UNIQUE INDEX global_perm_override_pkey ON public.global_perm_override USING btree (id);

alter table "public"."global_perm_override" add constraint "global_perm_override_pkey" PRIMARY KEY using index "global_perm_override_pkey";

alter table "public"."global_perm_override" add constraint "global_perm_override_id_fkey" FOREIGN KEY (id) REFERENCES users(id) not valid;

alter table "public"."global_perm_override" validate constraint "global_perm_override_id_fkey";

grant delete on table "public"."global_perm_override" to "anon";

grant insert on table "public"."global_perm_override" to "anon";

grant references on table "public"."global_perm_override" to "anon";

grant select on table "public"."global_perm_override" to "anon";

grant trigger on table "public"."global_perm_override" to "anon";

grant truncate on table "public"."global_perm_override" to "anon";

grant update on table "public"."global_perm_override" to "anon";

grant delete on table "public"."global_perm_override" to "authenticated";

grant insert on table "public"."global_perm_override" to "authenticated";

grant references on table "public"."global_perm_override" to "authenticated";

grant select on table "public"."global_perm_override" to "authenticated";

grant trigger on table "public"."global_perm_override" to "authenticated";

grant truncate on table "public"."global_perm_override" to "authenticated";

grant update on table "public"."global_perm_override" to "authenticated";

grant delete on table "public"."global_perm_override" to "service_role";

grant insert on table "public"."global_perm_override" to "service_role";

grant references on table "public"."global_perm_override" to "service_role";

grant select on table "public"."global_perm_override" to "service_role";

grant trigger on table "public"."global_perm_override" to "service_role";

grant truncate on table "public"."global_perm_override" to "service_role";

grant update on table "public"."global_perm_override" to "service_role";


