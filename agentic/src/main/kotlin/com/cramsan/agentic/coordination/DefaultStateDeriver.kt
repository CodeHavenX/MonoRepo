package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.vcs.VcsProvider
import java.nio.file.Files
import java.nio.file.Path

class DefaultStateDeriver(
    private val vcsProvider: VcsProvider,
    private val worktreeManager: WorktreeManager,
    private val agenticDir: Path,
) : StateDeriver {

    override suspend fun statusOf(task: Task, resolvedDependencies: Map<String, TaskStatus>): TaskStatus {
        val branchName = "agentic/${task.id}"

        // 1. Check for merged PR → DONE
        val mergedPrs = vcsProvider.listMergedPullRequests(labels = listOf("agentic-code"))
        if (mergedPrs.any { it.sourceBranch == branchName }) {
            return TaskStatus.DONE
        }

        // 2. Check failed.txt → FAILED
        val failedFile = agenticDir.resolve("tasks/${task.id}/failed.txt")
        if (Files.exists(failedFile)) {
            return TaskStatus.FAILED
        }

        // 3. Check open PR with changes requested → IN_PROGRESS (agent needs to address feedback)
        val openPrs = vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))
        val openPr = openPrs.firstOrNull { it.sourceBranch == branchName }
        if (openPr != null) {
            val hasRequestedChanges = vcsProvider.pullRequestHasRequestedChanges(openPr.id)
            return if (hasRequestedChanges) TaskStatus.IN_PROGRESS else TaskStatus.IN_REVIEW
        }

        // 5. Check worktree → IN_PROGRESS
        if (worktreeManager.get(task.id) != null) {
            return TaskStatus.IN_PROGRESS
        }

        // 6. Check all dependencies are DONE → PENDING or BLOCKED
        if (task.dependencies.isEmpty()) {
            return TaskStatus.PENDING
        }

        val allDepsDone = task.dependencies.all { depId ->
            resolvedDependencies[depId] == TaskStatus.DONE
        }
        return if (allDepsDone) TaskStatus.PENDING else TaskStatus.BLOCKED
    }
}
