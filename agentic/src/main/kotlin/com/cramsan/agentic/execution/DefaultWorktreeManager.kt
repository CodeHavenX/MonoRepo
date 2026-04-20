package com.cramsan.agentic.execution

import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultWorktreeManager"

class DefaultWorktreeManager(
    private val repoRoot: Path,
    private val agenticDir: Path,
    private val baseBranch: String,
    private val shell: ShellRunner,
) : WorktreeManager {

    private fun worktreePath(taskId: String): Path = agenticDir.resolve("worktrees/$taskId")

    override fun getOrCreate(taskId: String): Worktree {
        val path = worktreePath(taskId)
        if (Files.isDirectory(path)) {
            logI(TAG, "Reusing existing worktree for task $taskId at $path")
            return Worktree(taskId = taskId, path = path, branchName = "agentic/$taskId")
        }
        logI(TAG, "Creating new worktree for task $taskId at $path")
        // Note: ShellRunner.run is suspend, so we need to call it from a coroutine context
        // WorktreeManager.getOrCreate is NOT suspend per the interface spec.
        // We use kotlinx.coroutines.runBlocking here because this is a blocking infrastructure call.
        kotlinx.coroutines.runBlocking {
            val result = shell.run(
                "git", "worktree", "add",
                "-b", "agentic/$taskId",
                path.toString(),
                baseBranch,
                workingDir = repoRoot.toString(),
            )
            if (result.exitCode != 0) {
                throw IllegalStateException("Failed to create worktree for $taskId: ${result.stderr}")
            }
        }
        return Worktree(taskId = taskId, path = path, branchName = "agentic/$taskId")
    }

    override fun get(taskId: String): Worktree? {
        val path = worktreePath(taskId)
        return if (Files.isDirectory(path)) {
            Worktree(taskId = taskId, path = path, branchName = "agentic/$taskId")
        } else {
            null
        }
    }

    override fun listAll(): List<Worktree> {
        val worktreesDir = agenticDir.resolve("worktrees")
        if (!Files.isDirectory(worktreesDir)) return emptyList()
        return Files.list(worktreesDir).use { stream ->
            stream
                .filter { Files.isDirectory(it) }
                .map { dir ->
                    val taskId = dir.fileName.toString()
                    Worktree(taskId = taskId, path = dir, branchName = "agentic/$taskId")
                }
                .toList()
        }
    }

    override fun delete(taskId: String) {
        val path = worktreePath(taskId)
        logI(TAG, "Deleting worktree for task $taskId at $path")
        kotlinx.coroutines.runBlocking {
            val removeResult = shell.run(
                "git", "worktree", "remove",
                "--force",
                path.toString(),
                workingDir = repoRoot.toString(),
            )
            if (removeResult.exitCode != 0) {
                logW(TAG, "git worktree remove failed for $taskId: ${removeResult.stderr}")
            }
            val branchResult = shell.run(
                "git", "branch", "-D", "agentic/$taskId",
                workingDir = repoRoot.toString(),
            )
            if (branchResult.exitCode != 0) {
                logW(TAG, "git branch -D failed for $taskId: ${branchResult.stderr}")
            }
        }
        // Clean up directory if it still exists
        if (Files.isDirectory(path)) {
            path.toFile().deleteRecursively()
        }
    }
}
