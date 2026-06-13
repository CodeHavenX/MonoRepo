-- Configure the `flyer-files` storage bucket: cap object size and restrict content types to
-- the set Supabase will enforce on every upload, including signed-upload URLs.
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'flyer-files',
    'flyer-files',
    false,
    10485760, -- 10 MB
    ARRAY['image/jpeg', 'image/png', 'image/webp', 'application/pdf']
)
ON CONFLICT (id) DO UPDATE SET
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- Revoke all schema-level privileges from anon and authenticated on storage tables.
-- All application access goes through the Ktor backend using the service role key,
-- which bypasses RLS and GRANT restrictions entirely. No direct client access is permitted.
REVOKE ALL ON ALL TABLES IN SCHEMA storage FROM anon;
REVOKE ALL ON ALL TABLES IN SCHEMA storage FROM authenticated;

-- Prevent future tables in this schema from automatically inheriting grants.
ALTER DEFAULT PRIVILEGES IN SCHEMA storage REVOKE ALL ON TABLES FROM anon;
ALTER DEFAULT PRIVILEGES IN SCHEMA storage REVOKE ALL ON TABLES FROM authenticated;
