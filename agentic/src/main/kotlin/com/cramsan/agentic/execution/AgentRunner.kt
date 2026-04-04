package com.cramsan.agentic.execution

import com.cramsan.agentic.core.Task

sealed class AgentResult {
    data class PrOpened(val prId: String, val prUrl: String) : AgentResult()
    data class Failed(val reason: String) : AgentResult()
}

interface AgentRunner {
    suspend fun run(task: Task, worktree: Worktree): AgentResult
}
