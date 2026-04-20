package com.cramsan.agentic.vcs.fake

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.logD
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "FakeVcsProvider"

class FakeVcsProvider(
    private val autoMergeOnCreate: Boolean = false,
) : VcsProvider {
    val pullRequests: MutableList<PullRequest> = CopyOnWriteArrayList()
    val comments: MutableMap<String, MutableList<PullRequestComment>> = ConcurrentHashMap()
    val requestedChangesForPr: MutableSet<String> = ConcurrentHashMap.newKeySet()
    private val nextPrId = AtomicInteger(1)

    override suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String>,
    ): PullRequest {
        logD(TAG, "createPullRequest: sourceBranch=$sourceBranch, targetBranch=$targetBranch, title='$title', labels=$labels")
        val id = nextPrId.getAndIncrement()
        val autoMerge = autoMergeOnCreate && "agentic-code" in labels
        val pr = PullRequest(
            id = id.toString(),
            url = "https://github.com/fake/repo/pull/$id",
            title = title,
            state = if (autoMerge) PullRequestState.MERGED else PullRequestState.OPEN,
            sourceBranch = sourceBranch,
            targetBranch = targetBranch,
            labels = labels,
        )
        pullRequests.add(pr)
        logD(TAG, "createPullRequest: created PR id=${pr.id}, url=${pr.url}")
        return pr
    }

    override suspend fun listOpenPullRequests(labels: List<String>): List<PullRequest> {
        logD(TAG, "listOpenPullRequests: labels=$labels")
        val result = pullRequests.filter { pr ->
            pr.state == PullRequestState.OPEN &&
                (labels.isEmpty() || labels.any { it in pr.labels })
        }
        logD(TAG, "listOpenPullRequests: returning ${result.size} PRs")
        return result
    }

    override suspend fun listMergedPullRequests(labels: List<String>): List<PullRequest> {
        logD(TAG, "listMergedPullRequests: labels=$labels")
        val result = pullRequests.filter { pr ->
            pr.state == PullRequestState.MERGED &&
                (labels.isEmpty() || labels.any { it in pr.labels })
        }
        logD(TAG, "listMergedPullRequests: returning ${result.size} PRs")
        return result
    }

    override suspend fun getPullRequestComments(prId: String): List<PullRequestComment> {
        logD(TAG, "getPullRequestComments: prId=$prId")
        val result = comments[prId] ?: emptyList()
        logD(TAG, "getPullRequestComments: prId=$prId, returning ${result.size} comments")
        return result
    }

    override suspend fun addPullRequestComment(prId: String, body: String) {
        logD(TAG, "addPullRequestComment: prId=$prId, bodyLength=${body.length}")
        comments.getOrPut(prId) { CopyOnWriteArrayList() }.add(
            PullRequestComment(
                author = "agentic-bot",
                body = body,
                createdAtEpochMs = System.currentTimeMillis(),
            )
        )
        logD(TAG, "addPullRequestComment: comment added to prId=$prId")
    }

    override suspend fun isPullRequestMerged(prId: String): Boolean {
        logD(TAG, "isPullRequestMerged: prId=$prId")
        val isMerged = pullRequests.first { it.id == prId }.state == PullRequestState.MERGED
        logD(TAG, "isPullRequestMerged: prId=$prId, isMerged=$isMerged")
        return isMerged
    }

    override suspend fun pullRequestHasRequestedChanges(prId: String): Boolean {
        logD(TAG, "pullRequestHasRequestedChanges: prId=$prId")
        val hasChanges = prId in requestedChangesForPr
        logD(TAG, "pullRequestHasRequestedChanges: prId=$prId, hasRequestedChanges=$hasChanges")
        return hasChanges
    }

    // Test helpers
    fun mergePullRequest(prId: String) {
        logD(TAG, "mergePullRequest (test helper): prId=$prId")
        val index = pullRequests.indexOfFirst { it.id == prId }
        if (index >= 0) {
            pullRequests[index] = pullRequests[index].copy(state = PullRequestState.MERGED)
            logD(TAG, "mergePullRequest: PR prId=$prId set to MERGED")
        } else {
            logD(TAG, "mergePullRequest: PR prId=$prId not found")
        }
    }

    fun requestChanges(prId: String) {
        logD(TAG, "requestChanges (test helper): prId=$prId")
        requestedChangesForPr.add(prId)
        logD(TAG, "requestChanges: prId=$prId added to requestedChangesForPr")
    }
}
