package com.cramsan.agentic.execution

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultAgentRunner"

class DefaultAgentRunner(
    private val agentSession: AgentSession,
    private val vcsProvider: VcsProvider,
    private val reviewerAgents: List<ReviewerAgent>,
    private val reviewerLoader: ReviewerLoader,
    private val worktreeManager: WorktreeManager,
    private val agenticDir: Path,
) : AgentRunner {

    override suspend fun run(task: Task, worktree: Worktree): AgentResult {
        return try {
            val result = withTimeout(task.timeoutSeconds * 1_000L) {
                agentSession.execute(task, worktree)
            }

            when (result) {
                is AgentResult.PrOpened -> {
                    runReviewerAgents(task, result.prId)
                    result
                }
                is AgentResult.Failed -> {
                    writeFailedMarker(task.id, result.reason)
                    result
                }
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            val reason = "Agent exceeded timeout of ${task.timeoutSeconds}s"
            logW(TAG, reason)
            writeFailedMarker(task.id, reason)
            AgentResult.Failed(reason)
        } catch (e: Exception) {
            val reason = "Unexpected exception: ${e.message}"
            logE(TAG, reason, e)
            writeFailedMarker(task.id, reason)
            AgentResult.Failed(reason)
        }
    }

    private suspend fun runReviewerAgents(task: Task, prId: String) {
        val definitions = reviewerLoader.loadAll()
        if (definitions.isEmpty() || reviewerAgents.isEmpty()) return

        logI(TAG, "Running ${definitions.size} reviewer agents for task ${task.id}")

        // Get the PR diff to pass to reviewers (use empty diff if unavailable)
        val diff = try {
            val openPrs = vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))
            val pr = openPrs.firstOrNull { it.id == prId }
            if (pr != null) "PR #${pr.id}: ${pr.title}" else ""
        } catch (e: Exception) {
            logW(TAG, "Could not fetch PR info for reviewer agents: ${e.message}")
            ""
        }

        coroutineScope {
            definitions.flatMap { definition ->
                reviewerAgents.map { agent ->
                    async {
                        try {
                            logI(TAG, "[REVIEW] Reviewer '${definition.name}' started review for task '${task.id}'.")
                            val feedback = agent.reviewCode(definition, task, diff)
                            val comment = formatReviewerComment(feedback)
                            vcsProvider.addPullRequestComment(prId, comment)
                            logI(TAG, "Posted reviewer feedback from ${definition.name} on PR $prId")
                            logI(TAG, "[REVIEW] Reviewer '${definition.name}' completed review for task '${task.id}'.")
                        } catch (e: Exception) {
                            logW(TAG, "Reviewer agent ${definition.name} failed: ${e.message}")
                        }
                    }
                }
            }.map { it.await() }
        }
    }

    private fun writeFailedMarker(taskId: String, reason: String) {
        val failedFile = agenticDir.resolve("tasks/$taskId/failed.txt")
        Files.createDirectories(failedFile.parent)
        Files.writeString(failedFile, reason)
        logI(TAG, "Written failed marker for task $taskId: $reason")
    }

    private fun formatReviewerComment(feedback: com.cramsan.agentic.core.ReviewerFeedback): String {
        return """<!-- agentic-reviewer: ${feedback.reviewerName} -->
## :mag: Code Review by ${feedback.reviewerName}

${feedback.content}
"""
    }
}
