package com.cramsan.agentic.vcs.github

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val TAG = "GitHubVcsProvider"

class GitHubVcsProvider(
    private val owner: String,
    private val repo: String,
    private val shell: ShellRunner,
    private val json: Json,
) : VcsProvider {

    override suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String>,
    ): PullRequest = withContext(Dispatchers.IO) {
        logI(TAG, "createPullRequest: repo=$owner/$repo, sourceBranch=$sourceBranch, targetBranch=$targetBranch, title='$title', labels=$labels")
        val args = mutableListOf(
            "gh", "pr", "create",
            "--title", title,
            "--body", body,
            "--base", targetBranch,
            "--head", sourceBranch,
            "--repo", "$owner/$repo",
            "--json", "number,url,title,state,headRefName,baseRefName,labels",
        )
        labels.forEach { label ->
            args.add("--label")
            args.add(label)
        }
        delay(5.seconds)
        val result = shell.run(*args.toTypedArray())
        if (result.exitCode != 0) {
            logE(TAG, "createPullRequest failed: exitCode=${result.exitCode}, stderr=${result.stderr}")
            throw VcsProviderException("Failed to create PR: ${result.stderr}", result.exitCode)
        }
        val pr = parsePullRequest(result.stdout)
        logI(TAG, "createPullRequest succeeded: prId=${pr.id}, url=${pr.url}")
        pr
    }

    override suspend fun listOpenPullRequests(labels: List<String>): List<PullRequest> =
        withContext(Dispatchers.IO) {
            logI(TAG, "listOpenPullRequests: repo=$owner/$repo, labels=$labels")
            val args = mutableListOf(
                "gh", "pr", "list",
                "--state", "open",
                "--repo", "$owner/$repo",
                "--json", "number,url,title,state,headRefName,baseRefName,labels",
            )
            if (labels.isNotEmpty()) {
                args.add("--label")
                args.add(labels.joinToString(","))
            }
            delay(5.seconds)
            val result = shell.run(*args.toTypedArray())
            if (result.exitCode != 0) {
                logE(TAG, "listOpenPullRequests failed: exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to list PRs: ${result.stderr}", result.exitCode)
            }
            val prs = parsePullRequestList(result.stdout)
            logI(TAG, "listOpenPullRequests succeeded: found ${prs.size} open PRs")
            logD(TAG, "listOpenPullRequests result: ${prs.map { it.id }}")
            prs
        }

    override suspend fun listMergedPullRequests(labels: List<String>): List<PullRequest> =
        withContext(Dispatchers.IO) {
            logI(TAG, "listMergedPullRequests: repo=$owner/$repo, labels=$labels")
            val args = mutableListOf(
                "gh", "pr", "list",
                "--state", "merged",
                "--repo", "$owner/$repo",
                "--json", "number,url,title,state,headRefName,baseRefName,labels",
            )
            if (labels.isNotEmpty()) {
                args.add("--label")
                args.add(labels.joinToString(","))
            }
            delay(5.seconds)
            val result = shell.run(*args.toTypedArray())
            if (result.exitCode != 0) {
                logE(TAG, "listMergedPullRequests failed: exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to list merged PRs: ${result.stderr}", result.exitCode)
            }
            val prs = parsePullRequestList(result.stdout)
            logI(TAG, "listMergedPullRequests succeeded: found ${prs.size} merged PRs")
            logD(TAG, "listMergedPullRequests result: ${prs.map { it.id }}")
            prs
        }

    override suspend fun getPullRequestComments(prId: String): List<PullRequestComment> =
        withContext(Dispatchers.IO) {
            logI(TAG, "getPullRequestComments: repo=$owner/$repo, prId=$prId")
            delay(5.seconds)
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "comments",
            )
            if (result.exitCode != 0) {
                logE(TAG, "getPullRequestComments failed: prId=$prId, exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to get PR comments: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val commentsArray = obj["comments"]?.jsonArray ?: run {
                logW(TAG, "getPullRequestComments: no 'comments' field in response for prId=$prId")
                return@withContext emptyList()
            }
            val comments = commentsArray.map { element ->
                val commentObj = element.jsonObject
                PullRequestComment(
                    author = commentObj["author"]?.jsonObject?.get("login")?.jsonPrimitive?.content ?: "",
                    body = commentObj["body"]?.jsonPrimitive?.content ?: "",
                    createdAtEpochMs = 0L,
                )
            }
            logI(TAG, "getPullRequestComments succeeded: prId=$prId, commentCount=${comments.size}")
            comments
        }

    override suspend fun addPullRequestComment(prId: String, body: String) {
        logI(TAG, "addPullRequestComment: repo=$owner/$repo, prId=$prId, bodyLength=${body.length}")
        withContext(Dispatchers.IO) {
            delay(5.seconds)
            val result = shell.run(
                "gh", "pr", "comment", prId,
                "--repo", "$owner/$repo",
                "--body", body,
            )
            if (result.exitCode != 0) {
                logE(TAG, "addPullRequestComment failed: prId=$prId, exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to add PR comment: ${result.stderr}", result.exitCode)
            }
            logI(TAG, "addPullRequestComment succeeded: prId=$prId")
        }
    }

    override suspend fun isPullRequestMerged(prId: String): Boolean =
        withContext(Dispatchers.IO) {
            logI(TAG, "isPullRequestMerged: repo=$owner/$repo, prId=$prId")
            delay(5.seconds)
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "mergedAt",
            )
            if (result.exitCode != 0) {
                logE(TAG, "isPullRequestMerged failed: prId=$prId, exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to check PR merge status: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val mergedAt = obj["mergedAt"]?.jsonPrimitive?.content
            val isMerged = mergedAt != null && mergedAt != "null"
            logI(TAG, "isPullRequestMerged result: prId=$prId, isMerged=$isMerged, mergedAt=$mergedAt")
            isMerged
        }

    override suspend fun pullRequestHasRequestedChanges(prId: String): Boolean =
        withContext(Dispatchers.IO) {
            logI(TAG, "pullRequestHasRequestedChanges: repo=$owner/$repo, prId=$prId")
            delay(5.seconds)
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "reviewDecision",
            )
            if (result.exitCode != 0) {
                logE(TAG, "pullRequestHasRequestedChanges failed: prId=$prId, exitCode=${result.exitCode}, stderr=${result.stderr}")
                throw VcsProviderException("Failed to check PR review decision: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val reviewDecision = obj["reviewDecision"]?.jsonPrimitive?.content
            val hasRequestedChanges = reviewDecision == "CHANGES_REQUESTED"
            logI(TAG, "pullRequestHasRequestedChanges result: prId=$prId, reviewDecision=$reviewDecision, hasRequestedChanges=$hasRequestedChanges")
            hasRequestedChanges
        }

    private fun parsePullRequest(jsonStr: String): PullRequest {
        logD(TAG, "parsePullRequest: parsing JSON response")
        val obj = json.parseToJsonElement(jsonStr).jsonObject
        return PullRequest(
            id = obj["number"]?.jsonPrimitive?.content ?: "",
            url = obj["url"]?.jsonPrimitive?.content ?: "",
            title = obj["title"]?.jsonPrimitive?.content ?: "",
            state = parseState(obj["state"]?.jsonPrimitive?.content ?: ""),
            sourceBranch = obj["headRefName"]?.jsonPrimitive?.content ?: "",
            targetBranch = obj["baseRefName"]?.jsonPrimitive?.content ?: "",
            labels = obj["labels"]?.jsonArray?.map { it.jsonObject["name"]?.jsonPrimitive?.content ?: "" } ?: emptyList(),
        )
    }

    private fun parsePullRequestList(jsonStr: String): List<PullRequest> {
        logD(TAG, "parsePullRequestList: parsing JSON array response")
        val array = json.parseToJsonElement(jsonStr).jsonArray
        return array.map { element ->
            val obj = element.jsonObject
            PullRequest(
                id = obj["number"]?.jsonPrimitive?.content ?: "",
                url = obj["url"]?.jsonPrimitive?.content ?: "",
                title = obj["title"]?.jsonPrimitive?.content ?: "",
                state = parseState(obj["state"]?.jsonPrimitive?.content ?: ""),
                sourceBranch = obj["headRefName"]?.jsonPrimitive?.content ?: "",
                targetBranch = obj["baseRefName"]?.jsonPrimitive?.content ?: "",
                labels = obj["labels"]?.jsonArray?.map { it.jsonObject["name"]?.jsonPrimitive?.content ?: "" } ?: emptyList(),
            )
        }
    }

    private fun parseState(state: String): PullRequestState {
        logD(TAG, "parseState: input='$state'")
        return when (state.uppercase()) {
            "OPEN" -> PullRequestState.OPEN
            "CLOSED" -> PullRequestState.CLOSED
            "MERGED" -> PullRequestState.MERGED
            else -> {
                logW(TAG, "parseState: unknown state '$state', defaulting to OPEN")
                PullRequestState.OPEN
            }
        }
    }
}
