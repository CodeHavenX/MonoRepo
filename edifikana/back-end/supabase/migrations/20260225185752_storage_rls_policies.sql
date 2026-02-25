-- ============================================================================
-- Storage RLS Policies Migration
-- ============================================================================
-- This migration enables Row Level Security (RLS) on storage buckets
-- Generated via: supabase db pull --local
--
-- Purpose: Allow authenticated users to upload/download files from storage
-- Frontend Impact: Enables direct file uploads from client to Supabase Storage
-- ============================================================================

-- ----------------------------------------------------------------------------
-- DOCUMENTS BUCKET POLICIES
-- ----------------------------------------------------------------------------
-- Allows authenticated users to manage documents in the 'private' folder
-- Path structure: documents/private/*

-- Allow authenticated users to update documents
create
policy "Give authenticated users access to Documents flreew_0"
  on "storage"."objects"
  as permissive
  for
update
    to authenticated
    using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to view/download documents
create
policy "Give authenticated users access to Documents flreew_1"
  on "storage"."objects"
  as permissive
  for
select
    to authenticated
    using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to delete documents
create
policy "Give authenticated users access to Documents flreew_2"
  on "storage"."objects"
  as permissive
  for delete
to authenticated
using (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to upload documents
  create
policy "Give authenticated users access to Documents flreew_3"
  on "storage"."objects"
  as permissive
  for insert
  to authenticated
with check (((bucket_id = 'documents'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- ----------------------------------------------------------------------------
-- IMAGES BUCKET POLICIES
-- ----------------------------------------------------------------------------
-- Allows authenticated users to manage images in the 'private' folder
-- Path structure: images/private/*
-- Used for: Property icons, event photos, user avatars, etc.

-- Allow authenticated users to view/download images
  create
policy "Give authenticated users access to Images 1ffg0oo_0"
  on "storage"."objects"
  as permissive
  for
select
    to authenticated
    using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to delete images
create
policy "Give authenticated users access to Images 1ffg0oo_1"
  on "storage"."objects"
  as permissive
  for delete
to authenticated
using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to update images
  create
policy "Give authenticated users access to Images 1ffg0oo_2"
  on "storage"."objects"
  as permissive
  for
update
    to authenticated
    using (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text)));


-- Allow authenticated users to upload images
create
policy "Give authenticated users access to Images 1ffg0oo_3"
  on "storage"."objects"
  as permissive
  for insert
  to authenticated
with check (((bucket_id = 'images'::text) AND ((storage.foldername(name))[1] = 'private'::text)));

-- ============================================================================
-- END OF MIGRATION
-- ============================================================================


