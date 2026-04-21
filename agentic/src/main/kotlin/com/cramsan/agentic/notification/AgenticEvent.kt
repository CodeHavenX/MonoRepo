package com.cramsan.agentic.notification

import com.cramsan.agentic.core.Task

/**
 * Significant orchestrator lifecycle events dispatched to [Notifier] to alert operators.
 * Events are fired at most once per [com.cramsan.agentic.coordination.Orchestrator.run] call.
 *
 * [TaskFailed] and [RunDeadlocked] are posted as PR comments by
 * [com.cramsan.agentic.notification.vcs.VcsCommentNotifier]. [RunCompleted] is only logged to
 * stdout — no VCS comment is posted on success to avoid noise on clean runs.
 */
sealed class AgenticEvent {
    /** Fired immediately after an agent reports failure or exceeds its timeout. */
    data class TaskFailed(val task: Task, val reason: String) : AgenticEvent()

    /**
     * Fired when no task is PENDING or IN_PROGRESS and no agent is actively running, but not
     * all tasks are DONE. This indicates an unresolvable state (e.g. all remaining tasks are
     * BLOCKED by a FAILED dependency).
     */
    data class RunDeadlocked(val blockedTasks: List<Task>, val failedTasks: List<Task>) : AgenticEvent()

    /** Fired when every task in the run has reached [com.cramsan.agentic.core.TaskStatus.DONE]. */
    data class RunCompleted(val completedTasks: List<Task>) : AgenticEvent()
}
