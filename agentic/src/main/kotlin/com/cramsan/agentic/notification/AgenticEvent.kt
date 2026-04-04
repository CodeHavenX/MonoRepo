package com.cramsan.agentic.notification

import com.cramsan.agentic.core.Task

sealed class AgenticEvent {
    data class TaskFailed(val task: Task, val reason: String) : AgenticEvent()
    data class RunDeadlocked(val blockedTasks: List<Task>, val failedTasks: List<Task>) : AgenticEvent()
    data class RunCompleted(val completedTasks: List<Task>) : AgenticEvent()
}
