package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus
import com.cramsan.agentic.execution.WorktreeManager
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultStateDeriver"

/**
 * Production [StateDeriver] that reads from [vcsProvider], [worktreeManager], and the
 * filesystem to determine a task's current status.
 *
 * **Status priority order** (first match wins):
 * 1. Merged PR for `agentic/{taskId}` → [com.cramsan.agentic.core.TaskStatus.DONE]
 * 2. `tasks/{taskId}/failed.txt` exists → [com.cramsan.agentic.core.TaskStatus.FAILED]
 * 3. Open PR with "changes requested" → [com.cramsan.agentic.core.TaskStatus.IN_PROGRESS]
 * 4. Open PR without changes requested → [com.cramsan.agentic.core.TaskStatus.IN_REVIEW]
 * 5. Worktree directory exists → [com.cramsan.agentic.core.TaskStatus.IN_PROGRESS]
 * 6. No dependencies → [com.cramsan.agentic.core.TaskStatus.PENDING]
 * 7. All dependencies DONE → [com.cramsan.agentic.core.TaskStatus.PENDING]
 * 8. `tasks/{taskId}/unblocked.txt` exists → [com.cramsan.agentic.core.TaskStatus.PENDING] (one-shot override, file deleted)
 * 9. Otherwise → [com.cramsan.agentic.core.TaskStatus.BLOCKED]
 *
 * **Side effect**: [statusOf] **deletes** `unblocked.txt` when it is found (step 8). This makes
 * the manual unblock a one-shot operation that does not persist across ticks.
 *
 * **[pullRequestHasRequestedChanges]** makes an additional VCS query per open PR, so it is only
 * called when an open PR is actually found for the task.
 */
class DefaultStateDeriver(
    private val vcsProvider: VcsProvider,
    private val worktreeManager: WorktreeManager,
    private val agenticDir: Path,
) : StateDeriver {

    override suspend fun fetchPrContext(): PrContext {
        logD(TAG, "fetchPrContext: fetching merged and open PRs with label 'agentic-code'")
        val mergedPrs = vcsProvider.listMergedPullRequests(labels = listOf("agentic-code"))
        val openPrs = vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))
        logD(TAG, "fetchPrContext: mergedPrs=${mergedPrs.size}, openPrs=${openPrs.size}")
        return PrContext(mergedPrs = mergedPrs, openPrs = openPrs)
    }

    override suspend fun statusOf(
        task: Task,
        resolvedDependencies: Map<String, TaskStatus>,
        prContext: PrContext,
    ): TaskStatus {
        val branchName = "agentic/${task.id}"
        logD(TAG, "statusOf called: taskId=${task.id}, branch=$branchName, resolvedDependencies=$resolvedDependencies")

        // 1. Check for merged PR → DONE
        logD(TAG, "Found ${prContext.mergedPrs.size} merged PRs with label 'agentic-code'")
        if (prContext.mergedPrs.any { it.sourceBranch == branchName }) {
            logI(TAG, "Task ${task.id} has a merged PR on branch $branchName — status: DONE")
            return TaskStatus.DONE
        }

        // 2. Check failed.txt → FAILED
        val failedFile = agenticDir.resolve("tasks/${task.id}/failed.txt")
        logD(TAG, "Checking failed.txt: $failedFile")
        if (Files.exists(failedFile)) {
            logI(TAG, "Task ${task.id} has failed.txt present — status: FAILED")
            return TaskStatus.FAILED
        }

        // 3. Check open PR with changes requested → IN_PROGRESS (agent needs to address feedback)
        logD(TAG, "Found ${prContext.openPrs.size} open PRs with label 'agentic-code'")
        val openPr = prContext.openPrs.firstOrNull { it.sourceBranch == branchName }
        if (openPr != null) {
            logD(TAG, "Task ${task.id} has an open PR (id=${openPr.id}); checking for requested changes")
            val hasRequestedChanges = vcsProvider.pullRequestHasRequestedChanges(openPr.id)
            val derivedStatus = if (hasRequestedChanges) TaskStatus.IN_PROGRESS else TaskStatus.IN_REVIEW
            logI(TAG, "Task ${task.id} open PR hasRequestedChanges=$hasRequestedChanges — status: $derivedStatus")
            return derivedStatus
        }

        // 4. Check worktree → IN_PROGRESS
        logD(TAG, "Checking worktree for task ${task.id}")
        if (worktreeManager.get(task.id) != null) {
            logI(TAG, "Task ${task.id} has an active worktree — status: IN_PROGRESS")
            return TaskStatus.IN_PROGRESS
        }

        // 5. Check all dependencies are DONE → PENDING or BLOCKED
        if (task.dependencies.isEmpty()) {
            logI(TAG, "Task ${task.id} has no dependencies — status: PENDING")
            return TaskStatus.PENDING
        }

        logD(TAG, "Task ${task.id} dependencies: ${task.dependencies}, resolved: $resolvedDependencies")
        val allDepsDone = task.dependencies.all { depId ->
            resolvedDependencies[depId] == TaskStatus.DONE
        }
        if (allDepsDone) {
            logI(TAG, "Task ${task.id} all ${task.dependencies.size} dependencies are DONE — status: PENDING")
            return TaskStatus.PENDING
        }

        // Check for manual unblock marker
        val unblockedFile = agenticDir.resolve("tasks/${task.id}/unblocked.txt")
        logD(TAG, "Checking unblocked.txt: $unblockedFile")
        if (Files.exists(unblockedFile)) {
            logI(TAG, "Task ${task.id} has unblocked.txt — treating as PENDING (one-shot); deleting marker")
            Files.delete(unblockedFile)
            logD(TAG, "Deleted unblocked.txt for task ${task.id}")
            return TaskStatus.PENDING
        }

        val blockedDeps = task.dependencies.filter { resolvedDependencies[it] != TaskStatus.DONE }
        logW(TAG, "Task ${task.id} is blocked by unfinished dependencies: $blockedDeps — status: BLOCKED")
        return TaskStatus.BLOCKED
    }
}
