# CI/CD:

To be able to build and deploy our app through a CI pipeline we need to have access to the `google-services.json` files 
needed for Firebase. These files are not stored in the repository for security reasons. Instead, they are stored as
secrets. To provide these files, we will take an approach based on this [article](https://sheldon-okware.medium.com/want-to-add-google-services-json-into-your-github-actions-ci-cd-pipeline-e2c35b15456a).

## What you need
- Make sure you have followed the steps in the [Back-End README](../../../edifikana/back-end/README.md) to have the Firebase project set up.
- Once you have your project set up, you will need to download the `google-services.json` files for the different environments. In this guide we will have one for a Prod environment and one for a PreProd environment. 
- Make sure you have an [environment](https://github.com/CodeHavenX/MonoRepo/settings/environments) created, otherwise [create a new one](https://github.com/CodeHavenX/MonoRepo/settings/environments/new).

## Steps

To start lets encode the `google-services.json` files. We will use the `base64` command to encode the files. 

```bash
# Encode the Prod google-services.json files
base64 -i edifikana/front-end/app-android/src/preprod/google-services.json

# Encode the PreProd google-services.json files
base64 -i edifikana/front-end/app-android/src/prod/google-services.json
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
          DATA: ${{ secrets.GOOGLE_SERVICES_PREPROD }}
        run: echo $DATA | base64 -d > edifikana/front-end/app-android/src/preprod/google-services.json
```

This will read the secret and decode it to the `edifikana/front-end/app-android/src/preprod/google-services.json` file.

This step would have to done for each secret you have set up. In this case it would be for the Prod and PreProd environment files.