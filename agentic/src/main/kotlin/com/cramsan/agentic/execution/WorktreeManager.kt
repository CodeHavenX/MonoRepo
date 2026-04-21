package com.cramsan.agentic.execution

import java.nio.file.Path

/**
 * A git worktree checked out for a specific task. The [path] is an isolated directory within
 * `.agentic/worktrees/{taskId}/` where the agent reads and writes files without affecting the
 * main working tree or other agents. [branchName] always follows the pattern `agentic/{taskId}`.
 */
data class Worktree(
    val taskId: String,
    val path: Path,
    val branchName: String,
)

/**
 * Manages the lifecycle of git worktrees used to isolate concurrent agent executions.
 *
 * Each task that enters [com.cramsan.agentic.core.TaskStatus.IN_PROGRESS] gets its own worktree
 * via [getOrCreate]. The worktree is deleted (along with its branch) when the task reaches
 * [com.cramsan.agentic.core.TaskStatus.DONE] or [com.cramsan.agentic.core.TaskStatus.FAILED].
 *
 * **Status derivation**: [get] is called by [com.cramsan.agentic.coordination.DefaultStateDeriver]
 * on every poll tick to detect whether a worktree exists (one signal that a task is IN_PROGRESS).
 * It must be fast and non-blocking because it is called N times per tick (once per task).
 *
 * [listAll] scans the worktrees directory and is used for cleanup and diagnostics, not for
 * status derivation.
 */
interface WorktreeManager {
    /**
     * Returns the existing worktree for [taskId] if one is already on disk, or creates a new
     * one via `git worktree add -b agentic/{taskId}`. Idempotent: safe to call multiple times
     * for the same task.
     */
    suspend fun getOrCreate(taskId: String): Worktree

    /**
     * Returns the worktree for [taskId] if its directory exists on disk, or `null` otherwise.
     * This is a pure filesystem check — no git commands are run.
     */
    fun get(taskId: String): Worktree?

    /** Returns all worktrees currently present under the worktrees directory. */
    fun listAll(): List<Worktree>

    /**
     * Removes the worktree via `git worktree remove --force` and deletes the branch via
     * `git branch -D agentic/{taskId}`. Any errors are logged but do not throw; the worktree
     * directory is removed recursively as a fallback.
     */
    suspend fun delete(taskId: String)
}
