if [ -z "${PROJECT_ID}" ]; then
  echo "PROJECT_ID is not set"
  exit 1
fi

if [ -z "${SUPABASE_DB_PASSWORD}" ]; then
  echo "SUPABASE_DB_PASSWORD is not set"
  exit 1
fi

echo "Linking Edifikana project to Supabase"
cd edifikana/back-end || exit 1
echo "Current directory: $(pwd)"
supabase link --project-ref "$PROJECT_ID"
