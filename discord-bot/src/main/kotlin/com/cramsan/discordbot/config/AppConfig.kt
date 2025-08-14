package com.cramsan.discordbot.config

/**
 * Configuration for the Discord bot application.
 */
data class AppConfig(
    val discordToken: String,
    val githubToken: String?,
    val serverPort: Int = 8080,
    val githubOwner: String = "CodeHavenX",
    val githubRepo: String = "MonoRepo"
)

/**
 * Load configuration from environment variables.
 */
fun loadConfig(): AppConfig {
    val discordToken = System.getenv("DISCORD_BOT_TOKEN")
        ?: throw IllegalStateException("DISCORD_BOT_TOKEN environment variable is required")
    
    val githubToken = System.getenv("GITHUB_TOKEN") // Optional for public repos
    
    val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
    
    val githubOwner = System.getenv("GITHUB_OWNER") ?: "CodeHavenX"
    val githubRepo = System.getenv("GITHUB_REPO") ?: "MonoRepo"
    
    return AppConfig(
        discordToken = discordToken,
        githubToken = githubToken,
        serverPort = serverPort,
        githubOwner = githubOwner,
        githubRepo = githubRepo
    )
}