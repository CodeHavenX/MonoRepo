package com.cramsan.agentic.vcs

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment

/**
 * Abstracts version-control and code-review platform operations needed by the orchestrator.
 *
 * The orchestrator calls [listMergedPullRequests] and [listOpenPullRequests] on every poll tick
 * to derive task status without reading any in-memory state. This makes the system crash-safe:
 * on restart, a fresh poll produces the same status as before the crash.
 *
 * All branches managed by the orchestrator follow the naming convention `agentic/{taskId}` and
 * all PRs carry the label `agentic-code`. Callers always pass this label to filter queries,
 * preventing interference with unrelated PRs in shared repositories.
 *
 * Implementations:
 * - [com.cramsan.agentic.vcs.github.GitHubVcsProvider]: delegates to the `gh` CLI
 * - [com.cramsan.agentic.vcs.local.LocalVcsProvider]: file-backed in-process emulation
 * - [com.cramsan.agentic.vcs.fake.FakeVcsProvider]: thread-safe in-memory stub for tests
 */
interface VcsProvider {
    /**
     * Opens a new PR from [sourceBranch] targeting [targetBranch]. Throws if the platform
     * rejects the request (e.g. a PR for that branch already exists).
     */
    suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String> = emptyList(),
    ): PullRequest

    /**
     * Returns all currently open PRs, optionally filtered to those carrying all of the given [labels].
     * Called on every orchestrator tick to detect tasks in [com.cramsan.agentic.core.TaskStatus.IN_REVIEW]
     * or awaiting feedback.
     */
    suspend fun listOpenPullRequests(labels: List<String> = emptyList()): List<PullRequest>

    /**
     * Returns all merged PRs, optionally filtered by [labels]. Called on every orchestrator tick
     * to detect tasks that have reached [com.cramsan.agentic.core.TaskStatus.DONE].
     */
    suspend fun listMergedPullRequests(labels: List<String> = emptyList()): List<PullRequest>

    /** Returns all comments on the specified PR, used to detect duplicate notification posts. */
    suspend fun getPullRequestComments(prId: String): List<PullRequestComment>

    /** Appends a new comment to the specified PR. Used by reviewer agents and the notifier. */
    suspend fun addPullRequestComment(prId: String, body: String)

    /**
     * Returns whether the PR has been merged. Prefer [listMergedPullRequests] for bulk checks;
     * this method is intended for single-PR point-in-time queries.
     */
    suspend fun isPullRequestMerged(prId: String): Boolean

    /**
     * Returns true if a human reviewer has requested changes on the PR. When true, the
     * orchestrator will re-launch an agent for that task to address the feedback.
     */
    suspend fun pullRequestHasRequestedChanges(prId: String): Boolean
}
