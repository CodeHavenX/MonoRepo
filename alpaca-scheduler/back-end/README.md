# Alpaca Scheduler Back-End

## Getting Started
1. Download Docker on your machine. We use [Docker Desktop](https://www.docker.com/products/docker-desktop/)
   - If you are unable to launch Docker Desktop when on Ubuntu 24.04, try the following:
   https://forums.docker.com/t/docker-desktop-not-working-on-ubuntu-24-04/141054/2
2. For Windows, you will need to edit some setting in Docker Desktop
   1. Open Setting > General
   2. Check `Expose daemon on tcp://localhost:2375 without TLS`
   3. Check `Add the *.docker.internal names to the host's /etc/hosts file (Requires password)`
   4. Apply and restart. This is important given supabase isn't fully compatible with Windows
3. Follow the steps [here](https://supabase.com/docs/guides/cli/getting-started) to install a local supabase instance.
4. Once you've installed supabase, `cd MonoRepo/alpaca-scheduler` and enter `supabase start` . This will be used for local development.

### Ubuntu Work Around for Docker Desktop
```bash
sudo sysctl -w kernel.apparmor_restrict_unprivileged_userns=0
sudo systemctl --user restart docker-desktop
```

## Running the Server

### Set the Supabase Environment Variables

Before running the server locally make sure that Supabase is running locally by following the steps above. Once Supabase
is running you will see the following output:

```bash
supabase start
```

```
Started supabase local development setup.

         API URL: http://127.0.0.1:54321
     GraphQL URL: http://127.0.0.1:54321/graphql/v1
  S3 Storage URL: http://127.0.0.1:54321/storage/v1/s3
          DB URL: postgresql://postgres:postgres@127.0.0.1:54322/postgres
      Studio URL: http://127.0.0.1:54323
    Inbucket URL: http://127.0.0.1:54324
      JWT secret: super-secret-jwt-token-with-at-least-32-characters-long
        anon key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6ImFub24iLCJleHAiOjE5ODM4MTI5OTZ9.CRXP1A7WOeoJeXxjNni43kdQwgnWNReilDMblYTn_I0
service_role key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImV4cCI6MTk4MzgxMjk5Nn0.EGIM96RAZx35lJzdJsyH-qQwv8Hdp7fsn3W0YpN81IU
   S3 Access Key: 625729a08b95bf1b7ff351a663f3a23c
   S3 Secret Key: 850181e4652dd023b7a98c58ae0d2d34bd487ee0cc3254aed6eda37307425907
       S3 Region: local
```

From this output you will need the `API URL` and the `service_role key`. Set these as environment variables in your terminal.
I recommend you them in a permanent location like your `.bashrc` or `.zshrc` file.

```bash
export ALPACA_SUPABASE_URL=http://127.0.0.1:54321
export ALPACA_SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZS1kZW1vIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImV4cCI6MTk4MzgxMjk5Nn0.EGIM96RAZx35lJzdJsyH-qQwv8Hdp7fsn3W0YpN81IU
```

The first time you set these values you will need to restart your terminal or run `source ~/.bashrc` or `source ~/.zshrc` to apply the changes.
If you are using IntelliJ, you can set these values in the run configuration as well. 

### Running the Server Application

You can either run the server through gradle or through IntelliJ. To run through gradle, run the following command:
```bash
./gradlew :alpaca-scheduler:back-end:run
```
or by using the main function in the [AlpacaSchedulerBackEnd.kt](src/main/kotlin/com/codehavenx/alpaca/backend/AlpacaSchedulerBackEnd.kt) file.

If you get an error about `ALPACA_SUPABASE_URL` or `ALPACA_SUPABASE_KEY` being null, make sure you have set the environment variables correctly. You may need to restart your terminal or IntelliJ.

Once the server is running, it will be available at `http://0.0.0.0:8282`.

## Managing Supabase Changes

Some useful references: 
 - [Supabase Local Development](https://supabase.com/docs/guides/cli/local-development) 
 - [Supabase CLI Reference](https://supabase.com/docs/reference/cli/introduction)

We are still currently in the early stages of development and are making changes to the database schema consistently.
The recommended approach to managing changes is to perform schema changes locally and to push those changes. 

**MORE CONTENT TO BE FILLED HERE 🔨🚧**