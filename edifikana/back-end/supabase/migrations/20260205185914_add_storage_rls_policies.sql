
  create policy "Give authenticated users access to Documents flreew_0"
  on "storage"."objects"
  as permissive
  for update
  to public
using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Documents flreew_1"
  on "storage"."objects"
  as permissive
  for select
  to public
using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Documents flreew_2"
  on "storage"."objects"
  as permissive
  for delete
  to public
using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Documents flreew_3"
  on "storage"."objects"
  as permissive
  for insert
  to public
with check (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Images 1ffg0oo_0"
  on "storage"."objects"
  as permissive
  for select
  to public
using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Images 1ffg0oo_1"
  on "storage"."objects"
  as permissive
  for delete
  to public
using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Images 1ffg0oo_2"
  on "storage"."objects"
  as permissive
  for update
  to public
using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



  create policy "Give authenticated users access to Images 1ffg0oo_3"
  on "storage"."objects"
  as permissive
  for insert
  to public
with check (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text) AND (auth.role() = 'authenticated'::text)));



