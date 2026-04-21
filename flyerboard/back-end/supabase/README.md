# FlyerBoard Supabase Setup

## Applying Migrations

Migrations in `migrations/` are applied manually via the Supabase CLI or dashboard.

```bash
supabase db push
```

Or apply individual files in order via the Supabase SQL editor.

## Storage Bucket Setup

Create a private storage bucket named **`flyer-files`** via the Supabase dashboard:

1. Go to **Storage** in the Supabase dashboard.
2. Click **New bucket**.
3. Set the bucket name to `flyer-files`.
4. Ensure **Public bucket** is **disabled** (private).
5. Click **Save**.

The backend generates signed URLs with a 1-hour lifetime for accessing files in this bucket.

## Bootstrapping the First Admin User

After a user signs up, manually promote them to admin via SQL:

```sql
UPDATE public.user_profiles
SET role = 'admin'
WHERE id = '<user-uuid>';
```

Replace `<user-uuid>` with the user's UUID from `auth.users`.

## Environment Variables

The backend requires the following environment variables (see `deploy/env.example`).
Variable names use the `FLYERBOARD_` prefix applied by `EnvironmentConfiguration`:

| Variable                     | Description                                     |
|------------------------------|-------------------------------------------------|
| `FLYERBOARD_SUPABASE_URL`    | Your Supabase project URL                       |
| `FLYERBOARD_SUPABASE_KEY`    | Supabase service role key (not the anon key)    |
