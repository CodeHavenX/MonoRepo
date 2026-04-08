package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgenticConfig(
    val agentPoolSize: Int,
    val defaultTaskTimeoutSeconds: Long = 3600L,
    val baseBranch: String,
    val docsDir: String,
    val aiProvider: AiProviderConfig = AiProviderConfig.ClaudeCli(),
    val vcsProvider: VcsProviderConfig,
    val workflow: WorkflowConfig = WorkflowConfig(),
)

@Serializable
sealed class AiProviderConfig {
    abstract val model: String

    @Serializable
    @SerialName("claude-api")
    data class ClaudeApi(
        override val model: String = "claude-opus-4-6",
        val anthropicApiKeyEnvVar: String = "ANTHROPIC_API_KEY",
    ) : AiProviderConfig()

    @Serializable
    @SerialName("claude-cli")
    data class ClaudeCli(
        override val model: String = "claude-opus-4-6",
        val cliPath: String = "claude",
    ) : AiProviderConfig()

    @Serializable
    @SerialName("fake")
    data class Fake(
        override val model: String = "fake-model",
        val mode: FakeMode = FakeMode.TEST,
        val delayMs: Long = 0L,
        val autoCompleteAfterTurns: Int = 5,
        val defaultTextResponse: String = "I understand. Let me continue working on this task.",
    ) : AiProviderConfig()
}

@Serializable
enum class FakeMode {
    @SerialName("test")
    TEST,
    @SerialName("demo")
    DEMO,
}

@Serializable
sealed class VcsProviderConfig {
    @Serializable
    @SerialName("github")
    data class GitHub(val owner: String, val repo: String) : VcsProviderConfig()
}
