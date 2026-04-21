package com.cramsan.agentic.execution

import com.cramsan.agentic.core.Task

/**
 * Drives the conversational AI loop for a single task execution. [AgentSession] is the core
 * agentic loop: it sends the task context to the AI, dispatches tool calls, accumulates
 * conversation history, and loops until the AI signals completion or failure via a terminal tool.
 *
 * The session runs entirely within the provided [worktree] — all file reads, writes, and shell
 * commands are relative to the worktree path. This isolation ensures parallel tasks cannot
 * interfere with each other's working directories.
 *
 * Timeout enforcement is handled by [AgentRunner], not by [AgentSession] itself.
 */
interface AgentSession {
    /**
     * Runs the agentic loop to completion for [task] inside [worktree]. Returns when the AI
     * invokes a terminal tool (`task_complete` → [AgentResult.PrOpened] or `task_failed` →
     * [AgentResult.Failed]), or when the loop detects an unrecoverable error.
     *
     * May throw for unexpected infrastructure failures (network, disk); callers ([AgentRunner])
     * are responsible for converting these to [AgentResult.Failed].
     */
    suspend fun execute(task: Task, worktree: Worktree): AgentResult
}
