package com.cramsan.agentic.vcs.github

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.VcsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
        val result = shell.run(*args.toTypedArray())
        if (result.exitCode != 0) {
            throw VcsProviderException("Failed to create PR: ${result.stderr}", result.exitCode)
        }
        parsePullRequest(result.stdout)
    }

    override suspend fun listOpenPullRequests(labels: List<String>): List<PullRequest> =
        withContext(Dispatchers.IO) {
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
            val result = shell.run(*args.toTypedArray())
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to list PRs: ${result.stderr}", result.exitCode)
            }
            parsePullRequestList(result.stdout)
        }

    override suspend fun listMergedPullRequests(labels: List<String>): List<PullRequest> =
        withContext(Dispatchers.IO) {
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
            val result = shell.run(*args.toTypedArray())
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to list merged PRs: ${result.stderr}", result.exitCode)
            }
            parsePullRequestList(result.stdout)
        }

    override suspend fun getPullRequestComments(prId: String): List<PullRequestComment> =
        withContext(Dispatchers.IO) {
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "comments",
            )
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to get PR comments: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val commentsArray = obj["comments"]?.jsonArray ?: return@withContext emptyList()
            commentsArray.map { element ->
                val commentObj = element.jsonObject
                PullRequestComment(
                    author = commentObj["author"]?.jsonObject?.get("login")?.jsonPrimitive?.content ?: "",
                    body = commentObj["body"]?.jsonPrimitive?.content ?: "",
                    createdAtEpochMs = 0L,
                )
            }
        }

    override suspend fun addPullRequestComment(prId: String, body: String) {
        withContext(Dispatchers.IO) {
            val result = shell.run(
                "gh", "pr", "comment", prId,
                "--repo", "$owner/$repo",
                "--body", body,
            )
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to add PR comment: ${result.stderr}", result.exitCode)
            }
        }
    }

    override suspend fun isPullRequestMerged(prId: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "mergedAt",
            )
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to check PR merge status: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val mergedAt = obj["mergedAt"]?.jsonPrimitive?.content
            mergedAt != null && mergedAt != "null"
        }

    override suspend fun pullRequestHasRequestedChanges(prId: String): Boolean =
        withContext(Dispatchers.IO) {
            val result = shell.run(
                "gh", "pr", "view", prId,
                "--repo", "$owner/$repo",
                "--json", "reviewDecision",
            )
            if (result.exitCode != 0) {
                throw VcsProviderException("Failed to check PR review decision: ${result.stderr}", result.exitCode)
            }
            val obj = json.parseToJsonElement(result.stdout).jsonObject
            val reviewDecision = obj["reviewDecision"]?.jsonPrimitive?.content
            reviewDecision == "CHANGES_REQUESTED"
        }

    private fun parsePullRequest(jsonStr: String): PullRequest {
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
        return when (state.uppercase()) {
            "OPEN" -> PullRequestState.OPEN
            "CLOSED" -> PullRequestState.CLOSED
            "MERGED" -> PullRequestState.MERGED
            else -> PullRequestState.OPEN
        }
    }
}
