package com.cramsan.agentic.vcs.local

import com.cramsan.agentic.core.PullRequest
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.PullRequestState
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.agentic.vcs.github.VcsProviderException
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.CoroutineDispatcher

private const val TAG = "LocalVcsProvider"

/**
 * File-backed [com.cramsan.agentic.vcs.VcsProvider] for local development and integration
 * testing where a real GitHub repository is not available.
 *
 * All state is persisted as JSON to [stateFile], which is read on every operation (no in-memory
 * cache). A [kotlinx.coroutines.sync.Mutex] serialises concurrent access so that multiple agent
 * coroutines can safely call this provider simultaneously.
 *
 * When [autoMerge] is `true`, every newly created PR is immediately merged via `git merge --no-ff`
 * and recorded with state [com.cramsan.agentic.core.PullRequestState.MERGED]. This removes the
 * human review step and is useful for fully-automated end-to-end tests.
 *
 * When [autoMerge] is `false`, [isPullRequestMerged] checks the real git merge history via
 * `git branch --merged`; this requires the calling process to have a valid git repository at
 * [repoRoot] and can be slow if many branches are present.
 */
class LocalVcsProvider(
    private val stateFile: Path,
    private val autoMerge: Boolean,
    private val repoRoot: Path,
    private val shell: ShellRunner,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher,
) : VcsProvider {

    private val mutex = Mutex()

    override suspend fun createPullRequest(
        sourceBranch: String,
        targetBranch: String,
        title: String,
        body: String,
        labels: List<String>,
    ): PullRequest = withContext(dispatcher) {
        logI(TAG, "createPullRequest: sourceBranch=$sourceBranch, targetBranch=$targetBranch, title='$title', labels=$labels, autoMerge=$autoMerge")
        mutex.withLock {
            val state = readState()
            val existing = state.prs.find {
                it.sourceBranch == sourceBranch && it.state == PullRequestState.OPEN
            }
            check(existing == null) {
                "An open PR already exists for branch '$sourceBranch' (prId=${existing?.id})"
            }
            val prId = state.nextPrId.toString()
            state.nextPrId++
            val prState = if (autoMerge) PullRequestState.MERGED else PullRequestState.OPEN
            val localPr = LocalPr(
                id = prId,
                sourceBranch = sourceBranch,
                targetBranch = targetBranch,
                title = title,
                body = body,
                state = prState,
                labels = labels,
            )
            if (autoMerge) {
                logI(TAG, "createPullRequest: autoMerge=true, merging $sourceBranch into $targetBranch")
                val result = shell.run(
                    "git", "merge", "--no-ff", sourceBranch,
                    workingDir = repoRoot.toString(),
                )
                if (result.exitCode != 0) {
                    logE(TAG, "createPullRequest: git merge failed: exitCode=${result.exitCode}, stderr=${result.stderr}")
                    throw VcsProviderException(
                        "Failed to merge branch '$sourceBranch' into '$targetBranch': ${result.stderr}",
                        result.exitCode,
                    )
                }
                logI(TAG, "createPullRequest: merge succeeded for prId=$prId")
            }
            state.prs.add(localPr)
            writeState(state)
            val pr = localPr.toPullRequest()
            logI(TAG, "createPullRequest: created local PR prId=$prId, state=${pr.state}")
            pr
        }
    }

    override suspend fun listOpenPullRequests(labels: List<String>): List<PullRequest> =
        withContext(dispatcher) {
            logI(TAG, "listOpenPullRequests: labels=$labels")
            val result = mutex.withLock { readState() }.prs
                .filter { pr ->
                    pr.state == PullRequestState.OPEN &&
                        (labels.isEmpty() || labels.any { it in pr.labels })
                }
                .map { it.toPullRequest() }
            logI(TAG, "listOpenPullRequests: returning ${result.size} open PRs")
            result
        }

    override suspend fun listMergedPullRequests(labels: List<String>): List<PullRequest> =
        withContext(dispatcher) {
            logI(TAG, "listMergedPullRequests: labels=$labels")
            val result = mutex.withLock { readState() }.prs
                .filter { pr ->
                    pr.state == PullRequestState.MERGED &&
                        (labels.isEmpty() || labels.any { it in pr.labels })
                }
                .map { it.toPullRequest() }
            logI(TAG, "listMergedPullRequests: returning ${result.size} merged PRs")
            result
        }

    override suspend fun getPullRequestComments(prId: String): List<PullRequestComment> =
        withContext(dispatcher) {
            logI(TAG, "getPullRequestComments: prId=$prId")
            val pr = mutex.withLock { readState() }.prs.find { it.id == prId }
                ?: throw IllegalArgumentException("PR not found: prId=$prId")
            val comments = pr.comments.map {
                PullRequestComment(
                    author = it.author,
                    body = it.body,
                    createdAtEpochMs = it.createdAtEpochMs,
                )
            }
            logI(TAG, "getPullRequestComments: prId=$prId, returning ${comments.size} comments")
            comments
        }

    override suspend fun addPullRequestComment(prId: String, body: String) {
        withContext(dispatcher) {
            logI(TAG, "addPullRequestComment: prId=$prId, bodyLength=${body.length}")
            mutex.withLock {
                val state = readState()
                val pr = state.prs.find { it.id == prId }
                    ?: throw IllegalArgumentException("PR not found: prId=$prId")
                pr.comments.add(
                    LocalComment(
                        author = "agentic-bot",
                        body = body,
                        createdAtEpochMs = System.currentTimeMillis(),
                    )
                )
                writeState(state)
            }
            logI(TAG, "addPullRequestComment: comment added to prId=$prId")
        }
    }

    override suspend fun isPullRequestMerged(prId: String): Boolean =
        withContext(dispatcher) {
            logI(TAG, "isPullRequestMerged: prId=$prId, autoMerge=$autoMerge")
            mutex.withLock {
                val state = readState()
                val pr = state.prs.find { it.id == prId }
                    ?: throw IllegalArgumentException("PR not found: prId=$prId")
                if (pr.state == PullRequestState.MERGED) {
                    logI(TAG, "isPullRequestMerged: prId=$prId already MERGED in state file")
                    return@withLock true
                }
                // For autoMerge=true the PR is always set to MERGED at creation time,
                // so reaching here means it is still OPEN — return false without a git check.
                if (autoMerge) {
                    logI(TAG, "isPullRequestMerged: autoMerge=true but prId=$prId is still OPEN, returning false")
                    return@withLock false
                }
                // autoMerge=false: check whether the branch has been merged via git
                logD(TAG, "isPullRequestMerged: checking git for merged branches, target=${pr.targetBranch}")
                val result = shell.run(
                    "git", "branch", "--merged", pr.targetBranch,
                    workingDir = repoRoot.toString(),
                )
                if (result.exitCode != 0) {
                    logE(TAG, "isPullRequestMerged: git branch --merged failed: exitCode=${result.exitCode}, stderr=${result.stderr}")
                    throw VcsProviderException(
                        "Failed to check merged branches: ${result.stderr}",
                        result.exitCode,
                    )
                }
                val mergedBranches = result.stdout.lines().map { it.trim().removePrefix("* ") }
                val isMerged = pr.sourceBranch in mergedBranches
                if (isMerged) {
                    logI(TAG, "isPullRequestMerged: prId=$prId branch ${pr.sourceBranch} found in merged branches, updating state")
                    pr.state = PullRequestState.MERGED
                    writeState(state)
                }
                logI(TAG, "isPullRequestMerged: prId=$prId, isMerged=$isMerged")
                isMerged
            }
        }

    override suspend fun pullRequestHasRequestedChanges(prId: String): Boolean =
        withContext(dispatcher) {
            logI(TAG, "pullRequestHasRequestedChanges: prId=$prId")
            val pr = mutex.withLock { readState() }.prs.find { it.id == prId }
                ?: throw IllegalArgumentException("PR not found: prId=$prId")
            logI(TAG, "pullRequestHasRequestedChanges: prId=$prId, hasRequestedChanges=${pr.hasRequestedChanges}")
            pr.hasRequestedChanges
        }

    private fun readState(): LocalPrState {
        if (!Files.exists(stateFile)) {
            logD(TAG, "readState: state file does not exist, returning empty state")
            return LocalPrState()
        }
        val content = Files.readString(stateFile)
        return json.decodeFromString(LocalPrState.serializer(), content)
    }

    private fun writeState(state: LocalPrState) {
        Files.createDirectories(stateFile.parent)
        Files.writeString(stateFile, json.encodeToString(LocalPrState.serializer(), state))
        logD(TAG, "writeState: state written to $stateFile")
    }
}

private fun LocalPr.toPullRequest() = PullRequest(
    id = id,
    url = "local://pr/$id",
    title = title,
    state = state,
    sourceBranch = sourceBranch,
    targetBranch = targetBranch,
    labels = labels,
)
