package com.cramsan.agentic.execution

import java.nio.file.Path

data class Worktree(
    val taskId: String,
    val path: Path,
    val branchName: String,
)

interface WorktreeManager {
    suspend fun getOrCreate(taskId: String): Worktree
    fun get(taskId: String): Worktree?
    fun listAll(): List<Worktree>
    suspend fun delete(taskId: String)
}
