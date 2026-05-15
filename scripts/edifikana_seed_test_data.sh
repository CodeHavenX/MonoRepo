#!/usr/bin/env bash
set -euo pipefail

# Guard: refuse to run against any non-local Supabase instance.
# This prevents accidental seeding of staging or production databases.
SUPABASE_URL="${SUPABASE_URL:-http://localhost:54321}"
if [[ "$SUPABASE_URL" != *"localhost"* && "$SUPABASE_URL" != *"127.0.0.1"* ]]; then
  echo "ERROR: SUPABASE_URL ($SUPABASE_URL) does not appear to be local."
  echo "Refusing to run seed script against a non-local instance."
  exit 1
fi

echo "Seeding Edifikana local database..."
cd edifikana/back-end || exit 1
echo "Current directory: $(pwd)"

# supabase db reset wipes the database and re-applies all migrations + seed.sql.
supabase db reset

echo ""
echo "Seed complete. Login via OTP — enter an email below, then check InBucket at http://localhost:54324 for the code."
echo "Example accounts:"
echo "  owner1.org1@dev.local  (OWNER in Sunset Property Management)"
echo "  cross.alpha@dev.local  (OWNER in Org 1, ADMIN in Org 3)"
echo "  admin1.org2@dev.local  (ADMIN in Coastal Living Properties)"
