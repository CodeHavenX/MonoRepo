package com.cramsan.agentic.vcs

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment

interface VcsProvider {
    suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String> = emptyList(),
    ): PullRequest

    suspend fun listOpenPullRequests(labels: List<String> = emptyList()): List<PullRequest>

    suspend fun listMergedPullRequests(labels: List<String> = emptyList()): List<PullRequest>

    suspend fun getPullRequestComments(prId: String): List<PullRequestComment>

    suspend fun addPullRequestComment(prId: String, body: String)

    suspend fun isPullRequestMerged(prId: String): Boolean

    suspend fun pullRequestHasRequestedChanges(prId: String): Boolean
}
