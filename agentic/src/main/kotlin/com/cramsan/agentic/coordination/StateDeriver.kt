package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus

interface StateDeriver {
    suspend fun statusOf(task: Task, resolvedDependencies: Map<String, TaskStatus> = emptyMap()): TaskStatus
}
