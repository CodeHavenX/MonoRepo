package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus

/**
 * Drives the autonomous execution phase of the agentic system. The orchestrator runs a
 * continuous polling loop that:
 *
 * 1. Derives the live status of every task from durable artifacts (PRs, filesystem markers).
 * 2. Launches agent coroutines for tasks that are [com.cramsan.agentic.core.TaskStatus.PENDING]
 *    or [com.cramsan.agentic.core.TaskStatus.IN_PROGRESS], up to [OrchestratorConfig.agentPoolSize].
 * 3. Cleans up completed worktrees.
 * 4. Detects termination conditions: all tasks [com.cramsan.agentic.core.TaskStatus.DONE]
 *    (success) or no progress possible (deadlock).
 *
 * **Crash safety**: all persistent state lives in git branches and the filesystem. If the
 * process is killed and restarted, [run] resumes correctly because [StateDeriver] re-derives
 * status from the same durable artifacts on every tick.
 *
 * **[status]** provides a one-shot snapshot for CLI inspection (`agentic run status`) without
 * starting the polling loop.
 */
interface Orchestrator {
    /**
     * Starts the polling loop and blocks until all tasks are done or the run deadlocks.
     * Sends an [com.cramsan.agentic.notification.AgenticEvent] via the configured
     * [com.cramsan.agentic.notification.Notifier] on completion or deadlock.
     */
    suspend fun run(config: OrchestratorConfig)

    /** Returns a point-in-time snapshot of every task's derived status. Non-blocking except for VCS queries. */
    suspend fun status(): Map<Task, TaskStatus>
}

/**
 * Tuning parameters for a single [Orchestrator.run] invocation.
 *
 * [agentPoolSize] caps the number of concurrently running agent coroutines. Each agent holds one
 * git worktree, so this directly bounds disk and memory usage.
 *
 * [pollIntervalSeconds] controls how often the orchestrator re-derives task statuses and checks
 * for available slots. A shorter interval increases responsiveness at the cost of more frequent
 * VCS API calls.
 */
data class OrchestratorConfig(
    val agentPoolSize: Int,
    val pollIntervalSeconds: Long = 30L,
    val baseBranch: String,
)
