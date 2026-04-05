package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgenticConfig(
    val agentPoolSize: Int,
    val defaultTaskTimeoutSeconds: Long = 3600L,
    val baseBranch: String,
    val claudeModel: String = "claude-opus-4-6",
    val docsDir: String,
    val aiProvider: AiProviderConfig = AiProviderConfig.ClaudeCli(),
    val vcsProvider: VcsProviderConfig,
)

@Serializable
sealed class AiProviderConfig {
    @Serializable
    @SerialName("claude-api")
    data class ClaudeApi(
        val anthropicApiKeyEnvVar: String = "ANTHROPIC_API_KEY",
    ) : AiProviderConfig()

    @Serializable
    @SerialName("claude-cli")
    data class ClaudeCli(
        val cliPath: String = "claude",
    ) : AiProviderConfig()
}

@Serializable
sealed class VcsProviderConfig {
    @Serializable
    @SerialName("github")
    data class GitHub(val owner: String, val repo: String) : VcsProviderConfig()
}
