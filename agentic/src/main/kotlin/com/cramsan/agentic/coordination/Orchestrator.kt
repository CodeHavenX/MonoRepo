package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.agentic.core.TaskStatus

interface Orchestrator {
    suspend fun run(config: OrchestratorConfig)
    suspend fun status(): Map<Task, TaskStatus>
}

data class OrchestratorConfig(
    val agentPoolSize: Int,
    val pollIntervalSeconds: Long = 30L,
    val baseBranch: String,
)
