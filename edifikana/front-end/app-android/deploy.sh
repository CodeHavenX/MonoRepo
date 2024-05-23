echo

if [ -z "${EDIFIKANA_STORE_PASSWORD}" ]; then
  echo "EDIFIKANA_STORE_PASSWORD is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_KEY_ALIAS}" ]; then
  echo "EDIFIKANA_KEY_ALIAS is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_KEY_PASSWORD}" ]; then
  echo "EDIFIKANA_KEY_PASSWORD is unset or set to the empty string"
  exit 1
fi

echo
echo "Building Edifikana Android App with the following parameters:"
echo

bundle install
if [ $? -ne 0 ]; then
    echo "Failed to install Ruby dependencies"
    exit 1
fi

bundle exec fastlane run validate_play_store_json_key
if [ $? -ne 0 ]; then
    echo "Failed to validate Play Store JSON key"
    exit 1
fi

read -p "Do you want start the deployment? (Y/N): " confirm && [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]] || exit 1

bundle exec fastlane deploy
if [ $? -ne 0 ]; then
    echo "Failed to deploy Edifikana Cloud Function"
    exit 1
fi
