package com.cramsan.agentic.execution

import com.cramsan.agentic.core.Task

/**
 * The outcome of a single agent run for a task. Returned by [AgentRunner.run] after the
 * agent session completes or is terminated.
 *
 * [PrOpened] means the agent successfully completed the task and opened a pull request. The
 * orchestrator will not re-run the agent for this task unless the PR receives "changes requested".
 *
 * [Failed] means the task could not be completed. [com.cramsan.agentic.execution.DefaultAgentRunner]
 * writes a `failed.txt` marker on failure so the orchestrator does not retry automatically.
 * Use `agentic run task retry <id>` to clear the marker and re-queue the task.
 */
sealed class AgentResult {
    /** The agent successfully completed the task and opened PR [prId] at [prUrl]. */
    data class PrOpened(val prId: String, val prUrl: String) : AgentResult()

    /** The agent could not complete the task. [reason] is written to `failed.txt` for operator inspection. */
    data class Failed(val reason: String) : AgentResult()
}

/**
 * Manages the full lifecycle of a single agent run: enforcing timeouts, handling failures,
 * persisting failure markers, and triggering post-PR reviewer agents.
 *
 * [AgentRunner] is the layer between the orchestrator (which decides *when* to run a task)
 * and [AgentSession] (which drives the conversational AI loop). Its responsibilities are:
 * - Wrapping [AgentSession.execute] with a [kotlinx.coroutines.withTimeout] guard.
 * - Writing `failed.txt` on any failure path (timeout, exception, or [AgentResult.Failed]).
 * - Launching reviewer agents in parallel after a PR is opened.
 *
 * Callers should not catch exceptions from [run]; all failure paths return [AgentResult.Failed].
 */
interface AgentRunner {
    /**
     * Executes the agent for [task] inside [worktree] and returns the outcome. Always returns
     * a value — never throws. Failure details are in [AgentResult.Failed.reason] and also
     * written to `tasks/{taskId}/failed.txt`.
     */
    suspend fun run(task: Task, worktree: Worktree): AgentResult
}
