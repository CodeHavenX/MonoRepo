-- Drop permissive storage policies added in 20260205191524 and updated in 20260228000001.
-- RLS stays enabled on storage.objects; no replacement policies = deny all for non-service-role connections.
DROP POLICY IF EXISTS "Give authenticated users access to Documents flreew_0" ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Documents flreew_1" ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Documents flreew_2" ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Documents flreew_3" ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Images 1ffg0oo_0"   ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Images 1ffg0oo_1"   ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Images 1ffg0oo_2"   ON storage.objects;
DROP POLICY IF EXISTS "Give authenticated users access to Images 1ffg0oo_3"   ON storage.objects;

-- Revoke all schema-level privileges from anon and authenticated on all tables.
-- All application access goes through the Ktor backend using the service role key,
-- which bypasses RLS and GRANT restrictions entirely. No direct client access is permitted.
REVOKE ALL ON ALL TABLES IN SCHEMA public  FROM anon;
REVOKE ALL ON ALL TABLES IN SCHEMA public  FROM authenticated;
REVOKE ALL ON ALL TABLES IN SCHEMA storage FROM anon;
REVOKE ALL ON ALL TABLES IN SCHEMA storage FROM authenticated;

-- Prevent future tables in these schemas from automatically inheriting grants.
ALTER DEFAULT PRIVILEGES IN SCHEMA public  REVOKE ALL ON TABLES FROM anon;
ALTER DEFAULT PRIVILEGES IN SCHEMA public  REVOKE ALL ON TABLES FROM authenticated;
ALTER DEFAULT PRIVILEGES IN SCHEMA storage REVOKE ALL ON TABLES FROM anon;
ALTER DEFAULT PRIVILEGES IN SCHEMA storage REVOKE ALL ON TABLES FROM authenticated;
