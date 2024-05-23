RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

../../gradlew buildFunction

if [ $? -ne 0 ]; then
    echo "Failed to build Cloud Function"
    exit 1
fi

echo

ADMIN_SDK_CREDS="src/main/resources/.secrets/firebase-adminsdk.json"

if [ -f "$ADMIN_SDK_CREDS" ]; then
  echo "Verified that $ADMIN_SDK_CREDS exists"
else
  echo "File $ADMIN_SDK_CREDS does not exist"
  exit 1
fi

GDRIVE_CREDS="src/main/resources/.secrets/gdrive-access-service.json"

if [ -f "$GDRIVE_CREDS" ]; then
  echo "Verified that GDRIVE_CREDS exists"
else
  echo "File $GDRIVE_CREDS does not exist"
  exit 1
fi

if [ -z "${EDIFIKANA_STORAGE_FOLDER_ID}" ]; then
  echo "EDIFIKANA_STORAGE_FOLDER_ID is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_TIME_CARD_SPREADSHEET_ID}" ]; then
  echo "EDIFIKANA_TIME_CARD_SPREADSHEET_ID is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_EVENT_LOG_SPREADSHEET_ID}" ]; then
  echo "EDIFIKANA_EVENT_LOG_SPREADSHEET_ID is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID}" ]; then
  echo "EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID is unset or set to the empty string"
  exit 1
fi

if [ -z "${EDIFIKANA_PROJECT_NAME}" ]; then
  echo "EDIFIKANA_PROJECT_NAME is unset or set to the empty string"
  exit 1
fi

ENTRY_POINT=com.cramsan.edifikana.server.CloudFirebaseApp
REGION=us-west1
RUNTIME=java17
MEMORY=512MB
SOURCE=build/deploy

echo
echo "Deploying Edifikana Cloud Function with the following parameters:"
echo
echo "ENTRY_POINT: $ENTRY_POINT"
echo "REGION: $REGION"
echo "RUNTIME: $RUNTIME"
echo "MEMORY: $MEMORY"
echo "SOURCE: $SOURCE"
echo "STORAGE_FOLDER_ID: $EDIFIKANA_STORAGE_FOLDER_ID"
echo "TIME_CARD_SPREADSHEET_ID: $EDIFIKANA_TIME_CARD_SPREADSHEET_ID"
echo "EVENT_LOG_SPREADSHEET_ID: $EDIFIKANA_EVENT_LOG_SPREADSHEET_ID"
echo "FORM_ENTRIES_SPREADSHEET_ID: $EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID"
echo "PROJECT_NAME: $EDIFIKANA_PROJECT_NAME"

echo
echo "GCloud CLI Project ID:"
GCLOUD_PROJECT_ID=`gcloud config get-value project`
echo "$GCLOUD_PROJECT_ID"
echo

if [ "$EDIFIKANA_PROJECT_NAME" != "$GCLOUD_PROJECT_ID" ]; then
  echo "EDIFIKANA_PROJECT_NAME does not match the current gcloud project ID. Please set the correct project ID in the environment variable EDIFIKANA_PROJECT_NAME."
  exit 1
fi

echo -e "${YELLOW}BEFORE CONTINUING, PLEASE VERIFY THAT THE PROJECT WAS COMPILED WITH THE SECRETS FOR THE RIGHT TARGET ENVIRONMENT!!!.${NC}"
echo

read -p "Do you want to continue? (Y/N): " confirm && [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]] || exit 1

gcloud functions deploy edifikana-cloud-function \
  --gen2 \
  --entry-point=$ENTRY_POINT \
  --runtime=$RUNTIME \
  --memory=$MEMORY \
  --region=$REGION \
  --source=$SOURCE \
  --set-env-vars STORAGE_FOLDER_ID="$EDIFIKANA_STORAGE_FOLDER_ID" \
  --set-env-vars TIME_CARD_SPREADSHEET_ID="$EDIFIKANA_TIME_CARD_SPREADSHEET_ID" \
  --set-env-vars EVENT_LOG_SPREADSHEET_ID="$EDIFIKANA_EVENT_LOG_SPREADSHEET_ID" \
  --set-env-vars FORM_ENTRIES_SPREADSHEET_ID="$EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID" \
  --set-env-vars PROJECT_NAME="$EDIFIKANA_PROJECT_NAME"
