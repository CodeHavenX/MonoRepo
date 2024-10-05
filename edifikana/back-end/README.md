# Pre-requisites
## Firebase
- Go to firebase
- Create a new Project
- Use default settings for analytics
- Register app:
- package name(should be the preprod flavor for staging and the prod falvor for the prod stack)
  -Do not download the json file yet
- Next
- Datasote
- Create database
- use default name, select us-west1 as location(VERY IMPORTANT, this location will be used in other steps)
- Start in Production mode
- Storage
- Get started
- Start in production mode
- LOcation will be set to us-west1(same as above)
- Auth
- Get started
- Enable annonimous sign in
- Enable Google sign in
- Set the display name accordingly
- Set the support email
- Save
- Go to project settings
- Add other users to the project
- Download the google-services.json
- Save the file in <FIND LOCATION!!!!> , include paths for flavors

- Download the Firebase Admin SDK credentials.
    - **BEWARE! Generating a new key will invalidate the previous key. If there is a key already in use, use that one instead to avoid breaking the system.**
    - Open the Project Settings in Firebase.
    - Go into Service Accounts.
    - Select `Firebase Admin SDK`.
    - `Generate new private key`. and store the file in a safe place.
    - Place this file in the `back-end/src/main/resources` folder and name it `firebase-admin-sdk.json`. <--- Update the folder path

- Go to Billing and set up a budget alert.

## Google Cloud Access
- Create a new project
- name it accordingly
- Select the project
- Go to [IAM](https://console.cloud.google.com/iam-admin/iam)
- Let's create a service account.
    - Go to `Service Accounts`.
    - Click `Create Service Account`.
    - Follow the instructions to create a service account. There is no need to add new roles at this moment.
        - Name it `gdrive-access-service`
        - Once the account is created, open the account's details page and open the `Keys` tab.
        - Click on `Add Key` and create a new key. This will download a JSON file.
        - Place this file in the `back-end/src/main/resources` folder and name it `gdrive-credentials.json`.
        - Make a note of the email for this service account.

## Google Drive
- Have a Google account with a Drive.
    - Create a folder in your Google Drive and copy the ID. This will be used to store files.
        - This folder will be used later in the `STORAGE_FOLDER_ID` environment variable.
    - Create a Google Sheet and copy the ID.
        - This will be used later in the `TIME_CARD_SPREADSHEET_ID` environment variables.
    - Create a Google Sheet and copy the ID.
        - This will be used later in the `EVENT_LOG_SPREADSHEET_ID` environment variables.
    - Now share the folder and the sheets with the service account email and assign it `editor` permissions.

## Google Drive and Sheets API
- https://developers.google.com/workspace/guides/get-started
- In the Google Cloud console, go to Menu menu > More products > Google Workspace > Product Library *warning page loads very slowly*
- Click on APIs on the left hand side
- Click on Google Drive API
- Click on Enable
- Go back to the API library
- Click on Google Sheets API
- Click on Enable**TODO: We need to have a way to verify that we have completed the API setup.**

## Android App
- Make sure to get the SHA1 signatures for your debug certificate:
    - https://developers.google.com/android/guides/client-auth
    - Add the debug SHA1 and SHA256 into the app setting in project settings. (TODO: do we need both or only SHA1?)
- Storage:
- Update the rules to be
```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2024, 6, 3);
    }
  }
}
```
- In the above example set the date to a small range like tomorrow. This is the deadline to configure persmissions.
- In the Database create a collection called `users`.
- Add your user to the allowlist:
    - Create a document with the document id of the email you want to add to the allowlist.
    - Add a single field called `id`, set this also to the email
- Launch the app
- Verify that the google sign in dialog appears and that you are able to sign in.
- If that is the case, change the rules to be:
```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

- Enable required indexes:
- Not all queries are can be run directly from the app, some require first to enable an index:
- Open the IDE and connect the logcat
- Open the app. Go into the Events page
- Select into any staff
- Look into the logs, there will be a message about missng index and an included link.
- Open the link.
- Ensure that the correct project is selected
- Create the indexes
- Creating the indexes should not take more than 5 minutes
- Up to now you will be able to access the app and the content will be stored in Firebase. But the content is not going into Google Drive just yet.

Storage
- Enable authenticated writes
- Go to Storage, Ruls
- Set this as the rules
```
rules_version = '2';

// Craft rules based on data in your Firestore database
// allow write: if firestore.get(
//    /databases/(default)/documents/users/$(request.auth.uid)).data.isAdmin;
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Google Cloud Function
- Have a [Google Cloud account](https://console.cloud.google.com/).
- Install the [gcloud CLI](https://cloud.google.com/sdk/docs/install).
- Go into the Google Cloud Console and select the project you created earlier.
- Go to the [Cloud Functions](https://console.cloud.google.com/functions) page.
- Click on Enable Billing
- Select a billing account and click on `Set Account`.
- Enabling billing will take a few minutes. IF after 5 minutes the page is still loading, refresh the page.
  - If Billing is not enabled, this could be due to some other issues. Here is a link that mentions a similar problem https://stackoverflow.com/questions/45265125/enabling-an-api-loading-forever.
  - Go to Billing Account and check if the billing account is enabled.
- In the [Cloud Functions](https://console.cloud.google.com/functions) page, click on Create Function.
  - A window may appear requesting to enable the API. Click on `Enable API`.
- Manually create a [Google Cloud Function](https://console.cloud.google.com/functions/).
    - Use the `gen2` environment.
    - Use the name `edifikana-cloud-function`.
    - Select a region that matches your needs. In this scenario we will use `us-west1`.
    - Set the trigger to `Cloud Firestore`.
    - Set the event type to `google.cloud.firestore.document.v1.written`.
    - Ensure that `Retry on failure` is disabled.
    - Open the `More Options` panel and set the `Region` to `us-west1`.
    - Select a `Service Account`. The default service account should be fine.
    - If there is any message about needing to enable APIs or adding permissions, do so.
    - Click `Next`.
    - Set the Runtime to `Java 17`.
    - Click `Deploy`.


- Set the error reporting
- Go to the [Error Reporting](https://console.cloud.google.com/errors) page.
- Select the project you want to observe. You dont need to set up monitoring for non-prod environments.
- Click on `Configure Notifications`. 
- Select an existing channel or `Manage Notification Channels` to create a new one.
- Click on `Save`.

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
gcloud functions deploy edifikana-cloud-function \
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

### Helper

```
export EDIFIKANA_PROJECT_NAME=edifikana-stage
export EDIFIKANA_STORAGE_FOLDER_ID=1ZRI7dP2X7VqwixGKz3OPJ6KB-uuDkkM1
export EDIFIKANA_TIME_CARD_SPREADSHEET_ID=1mDgyQtJV_EkCrikM5-lBufwmFGS5N8FxMUVYvdbXSuM
export EDIFIKANA_EVENT_LOG_SPREADSHEET_ID=1v-we-o55vEu8tGItH0EvY0v4IJIwAPtW75ZbdPPNyQA
export EDIFIKANA_FORM_ENTRIES_SPREADSHEET_ID=16Fqq_uC6fyQVEhn7wTbNVcCjFLWuvgS676ic2ALrOzk

./deploy.sh
```