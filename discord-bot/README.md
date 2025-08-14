# Discord Bot

A Discord bot that integrates with the GitHub API to provide repository information and functionality.

## Features

- 🤖 Discord slash commands
- 📋 Query open GitHub issues
- 🔧 Configurable via environment variables
- ⚡ Built with Ktor and Kord

## Available Commands

### `/issues [limit]`
Fetches and displays open issues from the configured GitHub repository.
- `limit`: Optional parameter to specify the number of issues to fetch (1-25, default: 5)

## Configuration

The bot is configured via environment variables:

### Required
- `DISCORD_BOT_TOKEN`: Your Discord bot token

### Optional
- `GITHUB_TOKEN`: GitHub personal access token (for higher rate limits and private repos)
- `GITHUB_OWNER`: GitHub repository owner (default: "CodeHavenX")
- `GITHUB_REPO`: GitHub repository name (default: "MonoRepo")
- `SERVER_PORT`: HTTP server port (default: 8080)

## Setup

1. Create a Discord application and bot at https://discord.com/developers/applications
2. Get your bot token and set the `DISCORD_BOT_TOKEN` environment variable
3. Invite the bot to your Discord server with the `applications.commands` scope
4. (Optional) Create a GitHub personal access token for higher API rate limits
5. Run the application

## Development

```bash
# Build the module
./gradlew discord-bot:build

# Run the application
./gradlew discord-bot:run

# Create distribution
./gradlew discord-bot:distTar
```

## API Endpoints

The bot also provides HTTP endpoints for monitoring:

- `GET /` - Basic status
- `GET /health` - Health check
- `GET /status` - Detailed configuration status