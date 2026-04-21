package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * Provider-agnostic representation of a pull request. The [id] value is provider-specific:
 * GitHub uses numeric PR numbers as strings; [com.cramsan.agentic.vcs.local.LocalVcsProvider]
 * uses a monotonically incrementing integer.
 *
 * The orchestrator uses [sourceBranch] (expected format `agentic/{taskId}`) and [labels]
 * (expected value `agentic-code`) to correlate PRs to tasks. Filtering by label prevents the
 * orchestrator from interfering with unrelated PRs in the same repository.
 */
@Serializable
data class PullRequest(
    val id: String,
    val url: String,
    val title: String,
    val state: PullRequestState,
    val sourceBranch: String,
    val targetBranch: String,
    val labels: List<String>,
)

/** Lifecycle state of a [PullRequest]. [CLOSED] is unused by the orchestrator but preserved for completeness. */
@Serializable
enum class PullRequestState { OPEN, CLOSED, MERGED }

/**
 * A comment posted on a [PullRequest].
 *
 * Note: [com.cramsan.agentic.vcs.github.GitHubVcsProvider] always sets [createdAtEpochMs] to `0`
 * because the GitHub CLI `gh pr view --json comments` response does not include a parsed timestamp.
 * // TODO: parse `createdAt` ISO-8601 from the GitHub response and populate this field correctly.
 */
@Serializable
data class PullRequestComment(
    val author: String,
    val body: String,
    val createdAtEpochMs: Long,
)
