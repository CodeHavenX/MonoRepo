# Pre-requisites
## Google Cloud
 - Have a [Google Cloud account](https://console.cloud.google.com/).
 - Install the [gcloud CLI](https://cloud.google.com/sdk/docs/install).
 - Manually create a [Google Cloud Function](https://console.cloud.google.com/functions/).
   - Use the `gen2` environment.
   - Select a region that matches your needs. In this scenario we will use `us-west1`.
   - Set the trigger to `Cloud Firestore`.
   - Set the event type to `google.cloud.firestore.document.v1.created`.
   - Ensure that `Retry on failure` is disabled.
   - Open the `More Options` panel and set the `Region` to `us-west1`.
   - Select a `Service Account`. The default service account should be fine. 
   - If there is any message about needing to enable APIs or adding permissions, do so.
   - Click `Next`.
   - Set the Runtime to `Java 17`.
   - Click `Deploy`.
 - Go to [IAM](https://console.cloud.google.com/iam-admin/iam) and let's create a service account.
   - Go to `Service Accounts`.
   - Click `Create Service Account`.
   - Follow the instructions to create a service account. There is no need to add new roles at this moment.
     - Once the account is created, open the account's details page and open the `Keys` tab.
     - Click on `Add Key` and create a new key. This will download a JSON file.
     - Place this file in the `src/main/resources` folder and name it `credentials.json`.
     - Make a note of the email for this service account.
## Firebase
- Download the Firebase Admin SDK credentials.
    - **BEWARE! Generating a new key will invalidate the previous key. If there is a key already in use, use that one instead to avoid breaking the system.**
    - Open the Project Settings in Firebase.
    - Go into Service Accounts.
    - Select `Firebase Admin SDK`.
    - `Generate new private key`. and store the file in a safe place.
    - Place this file in the `src/main/resources` folder and name it `firebase-admin-sdk.json`.
## Google Drive 
 - Have a Google account with a Drive.
   - Create a folder in your Google Drive and copy the ID. This will be used to store files.
     - This folder will be used later in the `STORAGE_FOLDER_ID` environment variable.
   - Create a Google Sheet and copy the ID.
     - This will be used later in the `TIME_CARD_SPREADSHEET_ID` environment variables.
   - Create a Google Sheet and copy the ID.
     - This will be used later in the `EVENT_LOG_SPREADSHEET_ID` environment variables.
   - Now share the folder and the sheets with the service account email and assign it `editor` permissions.

# Testing

You can test the function by running the main method:

```bash
./gradlew edifikana:back-end:run
```

TODO: We need to improve this section. 

# Deployment

To deploy the function, run the following command:

```bash
./gradlew edifikana:back-end:buildFunction
```

Now the function will be packaged in a fat jar and placed in the `build/deploy` folder. Now lets upload it and deploy it.
Make sure you have the IDs of the Google Drive folders and Google Sheets ready.

```
gcloud functions deploy function-1 \
  --gen2 \
  --entry-point=com.cramsan.edifikana.server.CloudFireController \
  --runtime=java17 \
  --memory=512MB \
  --region=us-west1 \
  --source=build/deploy \
  --set-env-vars STORAGE_FOLDER_ID=<FOLDER_ID> \
  --set-env-vars TIME_CARD_SPREADSHEET_ID=<TIME_CARD_SHEET_ID> \
  --set-env-vars EVENT_LOG_SPREADSHEET_ID=<EVENT_LOG_SHEET_ID>
```

Now the function is deployed. You can test it by sending a POST request to the function. You can use the following command to test it:

```
echo CosDCmJwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9kb2N1bWVudHMvdGltZUNhcmRSZWNvcmRzL0ROSV80NzIwMjIwMS1DTE9DS19PVVQtMTcxNDg5MTk2ORIcChZmYWxsYmFja0VtcGxveWVlSWRUeXBlEgJYABIeChhmYWxsYmFja0VtcGxveWVlSWRSZWFzb24SAlgAEjUKCGltYWdlVXJsEimKASZjbG9ja2lub3V0LzIwMjQtMDUtMDQtMjMtNTItNDctODQ2LmpwZxITCglldmVudFRpbWUSBhDB2dyxBhIhChtmYWxsYmFja0VtcGxveWVlSWRUeXBlT3RoZXISAlgAEhkKCWV2ZW50VHlwZRIMigEJQ0xPQ0tfT1VUEhoKFGZhbGxiYWNrRW1wbG95ZWVOYW1lEgJYABIlChJlbXBsb3llZURvY3VtZW50SWQSD4oBDEROSV80NzIwMjIwMRoMCMTZ3LEGEJjP06UDIgwIxNncsQYQmM/TpQM= | \
base64 -d | \
curl -m 70 -X POST https://us-west1-<PROJECT_ID>.cloudfunctions.net/<FUNCTION_NAME> \
-H "Authorization: bearer $(gcloud auth print-identity-token)" \
-H "Content-Type: application/json" \
-H "ce-id: 1234567890" \
-H "ce-specversion: 1.0" \
-H "ce-type: google.cloud.firestore.document.v1.created" \
-H "ce-time: 2020-08-08T00:11:44.895529672Z" \
-H "ce-source: //firestore.googleapis.com/projects/<PROJECT_ID>/databases/(default)" --data-binary @-
```