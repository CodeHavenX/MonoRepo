package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus

/**
 * Derives the current [com.cramsan.agentic.core.TaskStatus] of a [Task] purely from durable
 * external artifacts: VCS pull-request state, filesystem marker files, and worktree presence.
 *
 * **No in-memory state**: every call to [statusOf] reads current conditions and returns a fresh
 * result. This is what makes the orchestrator crash-safe — restarting and re-deriving produces
 * the same status as before the crash.
 *
 * **[fetchPrContext]** exists as a separate method so the orchestrator can batch a single VCS
 * list call per poll tick and pass the resulting snapshot to each [statusOf] call, rather than
 * issuing N individual list queries for N tasks.
 */
interface StateDeriver {
    /**
     * Fetches the current set of merged and open PRs from [com.cramsan.agentic.vcs.VcsProvider]
     * and returns a snapshot. This should be called once per orchestrator tick, not once per task.
     */
    suspend fun fetchPrContext(): PrContext

    /**
     * Derives the [com.cramsan.agentic.core.TaskStatus] of [task] using [prContext] (pre-fetched)
     * and [resolvedDependencies] (the already-derived statuses of this task's dependencies).
     *
     * [resolvedDependencies] must be populated for all IDs in [Task.dependencies]; missing entries
     * cause the task to be treated as [com.cramsan.agentic.core.TaskStatus.BLOCKED].
     * The orchestrator guarantees this ordering via topological traversal (Kahn's algorithm).
     */
    suspend fun statusOf(
        task: Task,
        resolvedDependencies: Map<String, TaskStatus> = emptyMap(),
        prContext: PrContext,
    ): TaskStatus
}
