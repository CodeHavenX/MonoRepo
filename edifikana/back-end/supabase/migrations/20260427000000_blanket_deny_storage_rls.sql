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
