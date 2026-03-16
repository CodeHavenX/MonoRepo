-- Remove stale hashedPassword entries from auth.users raw_user_meta_data.
-- Password management is now delegated entirely to Supabase Auth.
-- See: https://github.com/CodeHavenX/MonoRepo/issues/215
UPDATE auth.users
SET raw_user_meta_data = raw_user_meta_data - 'hashedPassword'
WHERE raw_user_meta_data ? 'hashedPassword';
