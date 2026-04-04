package com.cramsan.agentic.vcs.fake

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.VcsProvider

class FakeVcsProvider : VcsProvider {
    val pullRequests: MutableList<PullRequest> = mutableListOf()
    val comments: MutableMap<String, MutableList<PullRequestComment>> = mutableMapOf()
    val requestedChangesForPr: MutableSet<String> = mutableSetOf()
    private var nextPrId = 1

    override suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String>,
    ): PullRequest {
        val pr = PullRequest(
            id = nextPrId++.toString(),
            url = "https://github.com/fake/repo/pull/${nextPrId - 1}",
            title = title,
            state = PullRequestState.OPEN,
            sourceBranch = sourceBranch,
            targetBranch = targetBranch,
            labels = labels,
        )
        pullRequests.add(pr)
        return pr
    }

    override suspend fun listOpenPullRequests(labels: List<String>): List<PullRequest> {
        return pullRequests.filter { pr ->
            pr.state == PullRequestState.OPEN &&
                (labels.isEmpty() || labels.any { it in pr.labels })
        }
    }

    override suspend fun listMergedPullRequests(labels: List<String>): List<PullRequest> {
        return pullRequests.filter { pr ->
            pr.state == PullRequestState.MERGED &&
                (labels.isEmpty() || labels.any { it in pr.labels })
        }
    }

    override suspend fun getPullRequestComments(prId: String): List<PullRequestComment> {
        return comments[prId] ?: emptyList()
    }

    override suspend fun addPullRequestComment(prId: String, body: String) {
        comments.getOrPut(prId) { mutableListOf() }.add(
            PullRequestComment(
                author = "agentic-bot",
                body = body,
                createdAtEpochMs = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun isPullRequestMerged(prId: String): Boolean {
        return pullRequests.first { it.id == prId }.state == PullRequestState.MERGED
    }

    override suspend fun pullRequestHasRequestedChanges(prId: String): Boolean {
        return prId in requestedChangesForPr
    }

    // Test helpers
    fun mergePullRequest(prId: String) {
        val index = pullRequests.indexOfFirst { it.id == prId }
        if (index >= 0) {
            pullRequests[index] = pullRequests[index].copy(state = PullRequestState.MERGED)
        }
    }

    fun requestChanges(prId: String) {
        requestedChangesForPr.add(prId)
    }
}
