echo "Linking Edifikana project to Supabase"
cd edifikana/back-end || exit 1
echo "Current directory: $(pwd)"
supabase db push