package com.cramsan.agentic.core

import kotlinx.serialization.Serializable

/**
 * The atomic unit of work that an agent executes. Tasks are parsed from the task-list document
 * during the planning phase and persisted as individual JSON files under `.agentic/tasks/{id}/`.
 *
 * [dependencies] lists the IDs of tasks that must reach [TaskStatus.DONE] before this task
 * becomes [TaskStatus.PENDING]. A task with no dependencies is eligible to run immediately.
 *
 * [timeoutSeconds] is enforced by [com.cramsan.agentic.execution.DefaultAgentRunner]; if the
 * agent session exceeds it, the task is marked [TaskStatus.FAILED] and a `failed.txt` marker
 * is written so the orchestrator does not retry it automatically.
 */
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dependencies: List<String>,
    val timeoutSeconds: Long = 3600L,
)

/**
 * The lifecycle state of a [Task] as derived by [com.cramsan.agentic.coordination.StateDeriver]
 * on every orchestrator poll tick. Status is **never persisted** — it is always re-derived from
 * durable artifacts (PR state, filesystem markers, worktree presence).
 *
 * State transitions (typical forward path):
 * ```
 * BLOCKED → PENDING → IN_PROGRESS → IN_REVIEW → DONE
 *                   ↘ FAILED
 * ```
 *
 * - [PENDING]: all dependencies are [DONE] (or the task has none); an agent slot will be assigned.
 * - [IN_PROGRESS]: an agent worktree exists, or an open PR has "changes requested" and the agent
 *   must address feedback.
 * - [IN_REVIEW]: an open PR exists with no requested changes; no agent slot is consumed.
 * - [DONE]: the branch PR has been merged into the base branch.
 * - [BLOCKED]: one or more dependencies are not yet [DONE].
 * - [FAILED]: a `failed.txt` marker file exists under `.agentic/tasks/{id}/`. Cleared by
 *   `agentic run task retry <id>`.
 */
// NOT @Serializable — status is always derived, never persisted
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    IN_REVIEW,
    DONE,
    BLOCKED,
    FAILED,
}
