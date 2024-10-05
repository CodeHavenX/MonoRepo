
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS "pg_net" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgsodium" WITH SCHEMA "pgsodium";

COMMENT ON SCHEMA "public" IS 'standard public schema';

CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";

CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "pgjwt" WITH SCHEMA "extensions";

CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";

SET default_tablespace = '';

SET default_table_access_method = "heap";

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

ALTER TABLE "public"."event_log_entries" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."properties" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "name" "text" NOT NULL
);

ALTER TABLE "public"."properties" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."staff" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "id_type" "text" NOT NULL,
    "first_name" "text" NOT NULL,
    "last_name" "text" NOT NULL,
    "role" "text" NOT NULL,
    "property_id" "uuid" NOT NULL
);

ALTER TABLE "public"."staff" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."time_card_events" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "staff_id" "uuid" DEFAULT "gen_random_uuid"(),
    "fallback_staff_name" "text",
    "property_id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "type" "text" NOT NULL,
    "image_url" "text",
    "timestamp" bigint NOT NULL
);

ALTER TABLE "public"."time_card_events" OWNER TO "postgres";

CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" "uuid" DEFAULT "gen_random_uuid"() NOT NULL,
    "email" "text" NOT NULL
);

ALTER TABLE "public"."users" OWNER TO "postgres";

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

ALTER TABLE "public"."event_log_entries" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."properties" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."staff" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."time_card_events" ENABLE ROW LEVEL SECURITY;

ALTER TABLE "public"."users" ENABLE ROW LEVEL SECURITY;

CREATE PUBLICATION "logflare_pub" WITH (publish = 'insert, update, delete, truncate');

ALTER PUBLICATION "logflare_pub" OWNER TO "supabase_admin";

ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";

GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";

GRANT ALL ON TABLE "public"."event_log_entries" TO "anon";
GRANT ALL ON TABLE "public"."event_log_entries" TO "authenticated";
GRANT ALL ON TABLE "public"."event_log_entries" TO "service_role";

GRANT ALL ON TABLE "public"."properties" TO "anon";
GRANT ALL ON TABLE "public"."properties" TO "authenticated";
GRANT ALL ON TABLE "public"."properties" TO "service_role";

GRANT ALL ON TABLE "public"."staff" TO "anon";
GRANT ALL ON TABLE "public"."staff" TO "authenticated";
GRANT ALL ON TABLE "public"."staff" TO "service_role";

GRANT ALL ON TABLE "public"."time_card_events" TO "anon";
GRANT ALL ON TABLE "public"."time_card_events" TO "authenticated";
GRANT ALL ON TABLE "public"."time_card_events" TO "service_role";

GRANT ALL ON TABLE "public"."users" TO "anon";
GRANT ALL ON TABLE "public"."users" TO "authenticated";
GRANT ALL ON TABLE "public"."users" TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES  TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS  TO "service_role";

ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES  TO "service_role";

RESET ALL;
