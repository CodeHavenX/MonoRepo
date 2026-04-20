package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus

interface StateDeriver {
    suspend fun fetchPrContext(): PrContext

    suspend fun statusOf(
        task: Task,
        resolvedDependencies: Map<String, TaskStatus> = emptyMap(),
        prContext: PrContext,
    ): TaskStatus
}
