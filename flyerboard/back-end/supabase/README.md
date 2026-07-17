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

## Getting an access token for manual API testing

`scripts/supabase_get_access_token.sh` (repo root, project-independent — works the
same against any project's local Supabase instance) mints a real bearer token
without going through OTP/Inbucket:

```bash
TOKEN=$(./scripts/supabase_get_access_token.sh --reset-password -e user1@dev.local)
curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:9292/api/v1/flyers/mine
```

Run `./scripts/supabase_get_access_token.sh --help` for all modes (`--create` for a
throwaway user, `--signin` for a user that already has a password).

For Kotlin integration tests, `SupabaseIntegrationTest.createTestAuthSession(email)` /
`.signInAsSeededUser(userId, email)` do the same thing in-process.

## Environment Variables

The backend requires the following environment variables (see `deploy/env.example`).
Variable names use the `FLYERBOARD_` prefix applied by `EnvironmentConfiguration`:

| Variable                     | Description                                     |
|------------------------------|-------------------------------------------------|
| `FLYERBOARD_SUPABASE_URL`    | Your Supabase project URL                       |
| `FLYERBOARD_SUPABASE_KEY`    | Supabase service role key (not the anon key)    |
