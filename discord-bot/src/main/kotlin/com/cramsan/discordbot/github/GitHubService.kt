package com.cramsan.discordbot.github

import com.cramsan.discordbot.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Service for interacting with the GitHub API.
 */
class GitHubService(
    private val config: AppConfig,
    private val json: Json
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    /**
     * Fetches open issues from the configured GitHub repository.
     */
    suspend fun getOpenIssues(limit: Int = 10): Result<List<GitHubIssue>> = try {
        val issues: List<GitHubIssue> = client.get {
            url("https://api.github.com/repos/${config.githubOwner}/${config.githubRepo}/issues")
            parameter("state", "open")
            parameter("per_page", limit)
            parameter("sort", "created")
            parameter("direction", "desc")

            config.githubToken?.let { token ->
                header("Authorization", "Bearer $token")
            }
            header("Accept", "application/vnd.github.v3+json")
            header("User-Agent", "Discord-Bot")
        }.body()

        Result.success(issues)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Closes the HTTP client.
     */
    fun close() {
        client.close()
    }
}
