package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Root runtime configuration for the agentic system. Deserialized from `.agentic/config.json`
 * at startup by [com.cramsan.agentic.app.agenticModule].
 *
 * [agentPoolSize] caps concurrent agent coroutines. Each running agent holds one git worktree,
 * so this value also bounds disk and memory usage.
 *
 * [baseBranch] is the target branch for all agent PRs and the starting point for every new
 * worktree. Changing this after tasks have started running will leave orphaned worktrees.
 *
 * [docsDir] is stored as a string (not a [java.nio.file.Path]) for JSON serialization
 * compatibility. It is resolved relative to the working directory at runtime.
 * // TODO: consider making docsDir relative to agenticDir rather than the working directory
 * to avoid path-resolution ambiguity.
 */
@Serializable
data class AgenticConfig(
    val agentPoolSize: Int,
    val defaultTaskTimeoutSeconds: Long = 3600L,
    val baseBranch: String,
    val docsDir: String,
    val aiProvider: AiProviderConfig = AiProviderConfig.ClaudeCli(),
    val vcsProvider: VcsProviderConfig,
    val taskListProvider: TaskListProviderConfig = TaskListProviderConfig.Document(),
)

/**
 * Selects which [com.cramsan.agentic.ai.AiProvider] implementation is wired at startup.
 * All variants are always available in the binary; selection happens at config-parse time.
 *
 * Note: [com.cramsan.agentic.ai.claude.ClaudeAiProvider] has its own internal 4-attempt retry
 * loop for HTTP errors, and is additionally wrapped in [com.cramsan.agentic.ai.RetryingAiProvider]
 * for rate-limit errors. The two layers serve different failure modes but callers should be
 * aware that a single [chat] call may block for several minutes under sustained rate limiting.
 */
@Serializable
sealed class AiProviderConfig {
    abstract val model: String

    /** Direct Anthropic HTTP API access. Requires the [anthropicApiKeyEnvVar] env var to be set. */
    @Serializable
    @SerialName("claude-api")
    data class ClaudeApi(
        override val model: String = "claude-opus-4-6",
        val anthropicApiKeyEnvVar: String = "ANTHROPIC_API_KEY",
    ) : AiProviderConfig()

    /**
     * Delegates to the `claude` CLI binary installed on the host. Requires the `claude` CLI
     * to be available at [cliPath] and authenticated. When [fullAccess] is false (default),
     * only text-mode (no tools) is supported, suitable for validation/review workloads.
     */
    @Serializable
    @SerialName("claude-cli")
    data class ClaudeCli(
        override val model: String = "claude-opus-4-6",
        val cliPath: String = "claude",
        /**
         * When true, the CLI is invoked with --dangerously-skip-permissions and
         * --output-format json, allowing full autonomous agent execution.
         * Suitable for 'run start'. When false (default), tool use is not
         * supported and the provider is restricted to validation/review workloads.
         */
        val fullAccess: Boolean = false,
    ) : AiProviderConfig()

    /**
     * In-memory stub that returns canned responses. Used for integration testing and demos.
     * Despite being named "Fake", it is compiled into the production binary and selectable
     * via config — useful for dry-run scenarios without incurring API costs.
     */
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

/** Controls the behavior pattern of [com.cramsan.agentic.ai.fake.FakeAiProvider]. */
@Serializable
enum class FakeMode {
    @SerialName("test")
    TEST,
    @SerialName("demo")
    DEMO,
}

/** Selects which [com.cramsan.agentic.vcs.VcsProvider] implementation is wired at startup. */
@Serializable
sealed class VcsProviderConfig {
    /** Delegates all operations to the `gh` CLI against the specified GitHub repository. */
    @Serializable
    @SerialName("github")
    data class GitHub(val owner: String, val repo: String) : VcsProviderConfig()

    /**
     * File-backed in-process VCS emulation. Intended for integration tests and local development
     * where a real GitHub repository is not available.
     *
     * When [autoMerge] is true, PRs are merged immediately on creation via `git merge --no-ff`.
     * This is useful for end-to-end tests where human PR review is not part of the scenario.
     */
    @Serializable
    @SerialName("local")
    data class Local(
        val stateFile: String = ".agentic/local_prs.json",
        val autoMerge: Boolean = false,
    ) : VcsProviderConfig()
}

/**
 * Selects the source from which the orchestrator loads the task list at startup.
 * The choice is made once; changing this between runs requires restarting the orchestrator.
 */
@Serializable
sealed class TaskListProviderConfig {
    /**
     * Reads tasks from a markdown document (such as one generated by the workflow)
     * and persists each task as a JSON file under [tasksOutputDir].
     *
     * @property documentPath Path to the source markdown document, relative to agenticDir.
     * @property tasksOutputDir Directory for persisted task JSON files, relative to agenticDir.
     */
    @Serializable
    @SerialName("document")
    data class Document(
        val documentPath: String = "docs/task-list.md",
        val tasksOutputDir: String = "tasks/",
    ) : TaskListProviderConfig()

    /**
     * Uses an in-memory fake implementation. For testing only.
     */
    @Serializable
    @SerialName("fake")
    data object Fake : TaskListProviderConfig()
}
