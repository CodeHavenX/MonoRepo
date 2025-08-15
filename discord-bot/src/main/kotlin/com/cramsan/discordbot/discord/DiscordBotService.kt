package com.cramsan.discordbot.discord

import com.cramsan.discordbot.config.AppConfig
import com.cramsan.discordbot.github.GitHubService
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Discord bot service that handles Discord interactions and integrates with GitHub.
 */
class DiscordBotService(
    private val config: AppConfig,
    private val githubService: GitHubService,
    private val coroutineScope: CoroutineScope
) {
    private var kord: Kord? = null

    companion object {
        private const val MAX_ISSUES_LIMIT = 25
        private const val DEFAULT_ISSUES_LIMIT = 5
        private const val MIN_ISSUES_LIMIT = 1
    }

    /**
     * Starts the Discord bot.
     */
    suspend fun start() {
        kord = Kord(config.discordToken)

        setupCommands()
        setupEventHandlers()

        kord?.login()
    }

    /**
     * Sets up slash commands.
     */
    private suspend fun setupCommands() {
        kord?.createGlobalChatInputCommand(
            name = "issues",
            description = "Get open issues from the GitHub repository"
        ) {
            string("limit", "Number of issues to fetch (max $MAX_ISSUES_LIMIT)") {
                required = false
            }
        }
    }

    /**
     * Sets up event handlers for Discord interactions.
     */
    private fun setupEventHandlers() {
        kord?.on<GuildChatInputCommandInteractionCreateEvent> {
            when (interaction.invokedCommandName) {
                "issues" -> handleIssuesCommand()
            }
        }
    }

    /**
     * Handles the /issues command.
     */
    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleIssuesCommand() {
        val limitParam = interaction.command.options["limit"]?.value?.toString()
        val limit = limitParam?.toIntOrNull()?.coerceIn(MIN_ISSUES_LIMIT, MAX_ISSUES_LIMIT)
            ?: DEFAULT_ISSUES_LIMIT

        val response = interaction.deferPublicResponse()

        coroutineScope.launch {
            try {
                val result = githubService.getOpenIssues(limit)

                if (result.isSuccess) {
                    val issues = result.getOrNull().orEmpty()

                    if (issues.isEmpty()) {
                        response.respond {
                            content = "🎉 No open issues found in ${config.githubOwner}/${config.githubRepo}!"
                        }
                    } else {
                        val issuesList = issues.take(limit).joinToString("\n\n") { issue ->
                            val labels = if (issue.labels.isNotEmpty()) {
                                " [${issue.labels.joinToString(", ") { it.name }}]"
                            } else {
                                ""
                            }

                            "**#${issue.number}** ${issue.title}$labels\n" +
                                "👤 ${issue.user.login} • 🔗 ${issue.htmlUrl}"
                        }

                        val repoName = "${config.githubOwner}/${config.githubRepo}"
                        response.respond {
                            content = "📋 **Open Issues in $repoName** (${issues.size} found)\n\n$issuesList"
                        }
                    }
                } else {
                    val error = result.exceptionOrNull()
                    response.respond {
                        content = "❌ Failed to fetch issues: ${error?.message ?: "Unknown error"}"
                    }
                }
            } catch (e: Exception) {
                response.respond {
                    content = "❌ An error occurred: ${e.message}"
                }
            }
        }
    }

    /**
     * Stops the Discord bot.
     */
    suspend fun stop() {
        kord?.shutdown()
        githubService.close()
    }
}
