package com.cramsan.agentic.execution

import com.cramsan.agentic.core.Task

interface AgentSession {
    suspend fun execute(task: Task, worktree: Worktree): AgentResult
}
