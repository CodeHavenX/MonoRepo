CREATE TABLE IF NOT EXISTS "public"."event_log_entries" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "staff_id" "uuid" DEFAULT "gen_random_uuid"(),
    "fallback_staff_name" "text",
    "property_id" "uuid" DEFAULT "gen_random_uuid"(),
    "type" "text" NOT NULL,
    "fallback_event_type" "text",
    "timestamp" bigint NOT NULL,
    "title" "text" NOT NULL,
    "description" "text",
    "unit" "text" NOT NULL
);


CREATE TABLE IF NOT EXISTS "public"."properties" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "name" "text" NOT NULL
);


CREATE TABLE IF NOT EXISTS "public"."staff" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "id_type" "text" NOT NULL,
    "first_name" "text" NOT NULL,
    "last_name" "text" NOT NULL,
    "role" "text" NOT NULL,
    "property_id" "uuid" NOT NULL
);


CREATE TABLE IF NOT EXISTS "public"."time_card_events" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "staff_id" "uuid" DEFAULT "gen_random_uuid"(),
    "fallback_staff_name" "text",
    "property_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "type" "text" NOT NULL,
    "image_url" "text",
    "timestamp" bigint NOT NULL
);


CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "email" "text" NOT NULL
);


ALTER TABLE ONLY "public"."event_log_entries"
    ADD CONSTRAINT "event_log_entries_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."properties"
    ADD CONSTRAINT "properties_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."staff"
    ADD CONSTRAINT "staff_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."time_card_events"
    ADD CONSTRAINT "time_card_events_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_id_key" UNIQUE ("id");

ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

ALTER TABLE ONLY "public"."event_log_entries"
    ADD CONSTRAINT "event_log_entries_property_id_fkey" FOREIGN KEY ("property_id") REFERENCES "public"."properties"("id");

ALTER TABLE ONLY "public"."event_log_entries"
    ADD CONSTRAINT "event_log_entries_staff_id_fkey" FOREIGN KEY ("staff_id") REFERENCES "public"."staff"("id");

ALTER TABLE ONLY "public"."staff"
    ADD CONSTRAINT "staff_property_id_fkey" FOREIGN KEY ("property_id") REFERENCES "public"."properties"("id");

ALTER TABLE ONLY "public"."time_card_events"
    ADD CONSTRAINT "time_card_events_property_id_fkey" FOREIGN KEY ("property_id") REFERENCES "public"."properties"("id");

ALTER TABLE ONLY "public"."time_card_events"
    ADD CONSTRAINT "time_card_events_staff_id_fkey" FOREIGN KEY ("staff_id") REFERENCES "public"."staff"("id");