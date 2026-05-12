# Edifikana Back-End — Local Development Guide

## Prerequisites

- [Supabase CLI](https://supabase.com/docs/guides/cli) installed (`supabase --version`)
- Docker Desktop running

## Start local Supabase

```bash
cd edifikana/back-end
supabase start
```

The CLI prints the local API URL and anon key when ready.

## Apply migrations and seed data

From the **repo root**, run the seed script. It wipes the local database and
re-applies all migrations plus `seed.sql` from scratch:

```bash
./scripts/edifikana_seed_test_data.sh
```

The script aborts automatically if `SUPABASE_URL` does not point to a local
instance, so it cannot accidentally touch staging or production.

## Configure test credentials

```bash
cp edifikana/back-end/config.properties.integ.example \
   edifikana/back-end/config.properties.integ
```

Open `config.properties.integ` and fill in the values printed by `supabase start`:

```properties
supabase.url=http://localhost:54321
supabase.key=<anon key from supabase start output>
supabase.disable=false
```

## Seed credentials

All seeded users share the password **`SeedUser2024!`**. This is an intentionally
public, local-only development credential; do **not** use it in production.

Example accounts:

| Email | Role | Org |
|-------|------|-----|
| `owner1.org1@dev.local` | OWNER | Sunset Property Management |
| `admin1.org2@dev.local` | ADMIN | Coastal Living Properties |
| `manager1.org3@dev.local` | MANAGER | Mountain View Estates |
| `employee1.org4@dev.local` | EMPLOYEE | Urban Nest Realty |
| `cross.alpha@dev.local` | OWNER (Org 1), ADMIN (Org 3) | Multi-org |
| `cross.beta@dev.local` | ADMIN (Org 2), MANAGER (Org 4) | Multi-org |

The seed dataset contains: 4 organisations, 16 properties, 36 users, 107 units,
96 employees, plus tasks and event log entries across all properties.

## Run integration tests

**Use IntelliJ, not the CLI.** On Windows, Gradle cannot clean up
`build/test-results/integTest/binary/output.bin` between runs because the JVM
holds a file lock on the binary output. Running from IntelliJ avoids this issue.

Open `SupabaseEmployeeDatastoreIntegrationTest` (or any `*IntegrationTest` class)
in IntelliJ and use the green run gutter icon.

## Stop local Supabase

```bash
cd edifikana/back-end
supabase stop
```

## Re-seed from scratch

Re-run the seed script at any time to wipe and re-apply:

```bash
./scripts/edifikana_seed_test_data.sh
```
