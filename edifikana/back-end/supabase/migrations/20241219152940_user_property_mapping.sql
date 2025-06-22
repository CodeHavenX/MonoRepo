create table "public"."user_property_mapping" (
    "id" uuid not null default gen_random_uuid(),
    "user_id" uuid not null,
    "property_id" uuid not null
);
CREATE UNIQUE INDEX user_property_mapping_pkey ON public.user_property_mapping USING btree (id);
alter table "public"."user_property_mapping" add constraint "user_property_mapping_pkey" PRIMARY KEY using index "user_property_mapping_pkey";
alter table "public"."user_property_mapping" add constraint "user_property_mapping_property_id_fkey" FOREIGN KEY (property_id) REFERENCES properties(id) not valid;
alter table "public"."user_property_mapping" validate constraint "user_property_mapping_property_id_fkey";
alter table "public"."user_property_mapping" add constraint "user_property_mapping_user_id_fkey" FOREIGN KEY (user_id) REFERENCES users(id) not valid;
alter table "public"."user_property_mapping" validate constraint "user_property_mapping_user_id_fkey";
