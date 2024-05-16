# CI/CD:

## Firebase

To be able to build and deploy our app through a CI pipeline we need to have access to the `google-services.json` files 
needed for Firebase. These files are not stored in the repository for security reasons. Instead, they are stored as
secrets. To provide these files, we will take an approach based on this [article](https://sheldon-okware.medium.com/want-to-add-google-services-json-into-your-github-actions-ci-cd-pipeline-e2c35b15456a).

### What you need
- Make sure you have followed the steps in the [Back-End README](../../../edifikana/back-end/README.md) to have the Firebase project set up.
- Once you have your project set up, you will need to download the `google-services.json` files for the different environments. In this guide we will have one for a Prod environment and one for a PreProd environment. 
- Make sure you have an [environment](https://github.com/CodeHavenX/MonoRepo/settings/environments) created, otherwise [create a new one](https://github.com/CodeHavenX/MonoRepo/settings/environments/new).

### Steps

The `google-services.json` files will be located in the `edifikana/front-end/app-android/src/[flavor]/` folder. 
You can add them to your local environment by copying them to the respective folders. 

**VERIFY THAT THESE FILES ARE NOT BEING ADDED TO GIT. THEY SHOULD BE AUTOMATICALLY IGNORED, IF THAT IS NOT THE CASE 
UPDATE `.gitignore` WITH THE NEEDED RULES**. 

Now that you have the respective files you can encode them. We will use the `base64` command to encode the files.

```bash
# Encode the Prod google-services.json files
base64 -i edifikana/front-end/app-android/src/prod/google-services.json

# Encode the PreProd google-services.json files
base64 -i edifikana/front-end/app-android/src/preprod/google-services.json
```

Copy the output of the command and go to the [environments](https://github.com/CodeHavenX/MonoRepo/settings/environments/)
and then select the environment you want to add the secret to. 

Click on `Add environment secret`, set a name for the secret, for example `GOOGLE_SERVICES_PROD` and `GOOGLE_SERVICES_PREPROD`. 
On each secret paste the output of the respective command in the value field.

Now with the secrets set up, we can use them in our CI pipeline. In the [release action](../../../.github/workflows/buildRelease.yml)
file we can add the following step to load the `google-services.json` file.

```YAML
- name: Load Google Service file
  env:
    GOOGLE_SERVICES_PREPROD: ${{ secrets.GOOGLE_SERVICES_PREPROD }}
  run: echo GOOGLE_SERVICES_PREPROD | base64 -d > edifikana/front-end/app-android/src/preprod/google-services.json
```

This will read the secret and decode it to the `edifikana/front-end/app-android/src/preprod/google-services.json` file.

This step would have to done for each secret you have set up. In this case it would be for the Prod and PreProd environment files.

## Fastlane

In order to manage publishing the app to the Play Store, we use Fastlane. 
To set up Fastlane, follow the steps in the [Publishing guide](../../../edifikana/front-end/app-android/PUBLISHING.md).

Once you have setup Fastlane, you will end up with a secret key in the `.secrets/` folder. In this guide we will have
a file called `.secrets/silken-physics.json`.

**VERIFY THAT THIS FILE IS NOT BEING ADDED TO GIT. THEY SHOULD BE AUTOMATICALLY IGNORED, IF THAT IS NOT THE CASE
UPDATE `.gitignore` WITH THE NEEDED RULES**.

Now use the same approach as before to encode the file and upload it as a secret named `FASTLANE_SECRETS`.

```bash
# Encode the Prod google-services.json files
base64 -i edifikana/front-end/app-android/.secrets/silken-physics.json
```

And add the file as a step in the github actions.

```YAML
- name: Load Fastlane file
  env:
    FASTLANE_SECRETS: ${{ secrets.FASTLANE_SECRETS }}
  run: echo $DATA | base64 -d > edifikana/front-end/app-android/.secrets/silken-physics.json
```

## App Signing

To sign the app we need to have access to the keystore file. This file should not be included in the repo.
Instead, we will encode the file and upload it as a secret by using the method described above.

```bash
# Encode the Prod google-services.json files
base64 -i path/to/upload.jks
```

Now add the file as a step in the github actions.

```YAML
- name: Load Android upload signing key
  env:
    UPLOAD_KEY: ${{ secrets.UPLOAD_KEY }}
  run: echo $DATA | base64 -d > edifikana/front-end/app-android/.secrets/upload.jks
```

Finally, you will need to load three secret variables in the github actions. The
environment variables are `EDIFIKANA_STORE_PASSWORD`, `EDIFIKANA_KEY_ALIAS`, `EDIFIKANA_KEY_PASSWORD`.
The values of this variables are out of scope of this guide.

Finally, load the variables in the github actions.

```YAML
- name: Build with Gradle
  uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1
  env:
    EDIFIKANA_STORE_PASSWORD: ${{ secrets.EDIFIKANA_STORE_PASSWORD }}
    EDIFIKANA_KEY_ALIAS: ${{ secrets.EDIFIKANA_KEY_ALIAS }}
    EDIFIKANA_KEY_PASSWORD: ${{ secrets.EDIFIKANA_KEY_PASSWORD }}
  with:
    arguments: releaseAll
```