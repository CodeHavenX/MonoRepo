# Discord Bot Platform
 
This module provides a ktor-based service for managing Discord based interactions.
It uses a modular system to allow for extending adding new functionality.

## Getting started
 
To run this program you will need the following information:
- A discord Access Token. You can find the steps to get this token [here](https://github.com/kordlib/kord/wiki/Getting-Started).
- (Optional): A Google Cloud authentication mechanism to be able to use the [GoogleTranslateService](src/main/kotlin/com/codehavenx/platform/bot/service/google/GoogleTranslateService.kt). It is recommended to follow [these steps](https://cloud.google.com/docs/authentication/application-default-credentials#personal) and install the credentials locally using the gcloud CLI. 
  - When installing the gCloud CLI in Linux, you may encounter issues with expired public keys. I followed this solution to be able to refresh those keys:
    - https://stackoverflow.com/questions/49582490/running-apt-update-raises-gpg-error-cloud-sdk-is-not-signed
    - `wget https://packages.cloud.google.com/apt/doc/apt-key.gpg \
      && apt-key add apt-key.gpg`

## Design

This service uses Ktor as the webservice and core of this platform. 
We rely heavily on DI to avoid heavy-coupling of components, and to achieve this we use Koin as the dependency injection library. 

We split our components mostly in two types, **services** and **controllers**. 

Controllers define the network API and transport mechanisms to receive interactions, for example we can use controllers to define new Http routes, Webhook locations or Websocket paths. Controllers have very little business logic and they
mostly focus on translating data from external APIs to internal APIs. Controllers will then defer to services to perform more complex business logic.

Services are the core of the complexities of the functionality we provide in this platform. They are classes that do not expose any public network APIs. 

By separating our code this way we aim to provide a scalable and testable platform.

## How to extend

### Discord Intents

To be able to use [slash commands](https://discord.com/developers/docs/interactions/application-commands#:~:text=Slash%20commands%E2%80%94the%20CHAT_INPUT%20type,like%20arguments%20to%20a%20function) in Discord we provide the [InteractionModule.kt](src%2Fmain%2Fkotlin%2Fcom%2Fcodehavenx%2Fplatform%2Fbot%2Fcontroller%2Fkord%2FInteractionModule.kt) interface.

For an example of how this interface is implemented, you can look at [WebhookRegisterInteractionModule.kt](src%2Fmain%2Fkotlin%2Fcom%2Fcodehavenx%2Fplatform%2Fbot%2Fcontroller%2Fkord%2FWebhookRegisterInteractionModule.kt).

### Webhooks

To trigger an event based on a receiving Webhook, you can use [WebhookEntryPoint.kt](src%2Fmain%2Fkotlin%2Fcom%2Fcodehavenx%2Fplatform%2Fbot%2Fcontroller%2Fwebhook%2FWebhookEntryPoint.kt).

For an example of how this interface is implemented, look at [GithubCommitPushEntryPoint.kt](src%2Fmain%2Fkotlin%2Fcom%2Fcodehavenx%2Fplatform%2Fbot%2Fcontroller%2Fwebhook%2FGithubCommitPushEntryPoint.kt).

### Business logic

Most of the business logic of the interaction you need to implement will be in the [service](src%2Fmain%2Fkotlin%2Fcom%2Fcodehavenx%2Fplatform%2Fbot%2Fservice) folder.
There is no strong structure to these classes, as long as they follow the following rules.

- Do not expose public network APIs like HTTP APIs, sockets or websockets.
- Follow the DI pattern.
- These classes should be well covered in UTs.

### Testing

To be able to deal with the large scope of modules, we expect that all **controllers** and **services** are 
designed with testability in mind. 

## Installing
The `install.sh` script allows for installing this service in a Linux system. 

