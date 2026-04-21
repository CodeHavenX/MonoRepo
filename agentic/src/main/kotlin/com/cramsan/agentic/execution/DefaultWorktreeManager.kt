package com.cramsan.agentic.execution

import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultWorktreeManager"

/**
 * Production [WorktreeManager] that manages git worktrees under `.agentic/worktrees/`.
 *
 * **Branch naming**: every worktree uses branch `agentic/{taskId}`. If a branch of that name
 * already exists when [getOrCreate] is called, `git worktree add -b` will fail. This is
 * intentional: the branch is only deleted in [delete], so a surviving branch indicates a
 * worktree that was not properly cleaned up (e.g. after a crash).
 * // TODO: handle the "branch already exists" case in getOrCreate by checking for and reusing
 * the branch rather than failing, to improve crash recovery.
 *
 * **[get] is filesystem-only**: it checks `Files.isDirectory` without running any git commands.
 * This makes it fast enough to call on every orchestrator poll tick.
 *
 * **[delete] is best-effort**: errors from both `git worktree remove` and `git branch -D` are
 * logged but not thrown. If the worktree directory still exists after the git commands, it is
 * removed recursively as a fallback. Callers should not assume the branch is gone after [delete].
 */
class DefaultWorktreeManager(
    private val repoRoot: Path,
    private val agenticDir: Path,
    private val baseBranch: String,
    private val shell: ShellRunner,
) : WorktreeManager {

    private val worktreesDir: Path = agenticDir.resolve("worktrees")

    private fun worktreePath(taskId: String): Path = worktreesDir.resolve(taskId)

    override suspend fun getOrCreate(taskId: String): Worktree {
        val path = worktreePath(taskId)
        if (Files.isDirectory(path)) {
            logI(TAG, "Reusing existing worktree for task $taskId at $path")
            return Worktree(taskId = taskId, path = path, branchName = "agentic/$taskId")
        }
        logI(TAG, "Creating new worktree for task $taskId at $path")
        val result = shell.run(
            "git", "worktree", "add",
            "-b", "agentic/$taskId",
            path.toString(),
            baseBranch,
            workingDir = repoRoot.toString(),
        )
        check(result.exitCode == 0) { "Failed to create worktree for $taskId: ${result.stderr}" }
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

    override suspend fun delete(taskId: String) {
        val path = worktreePath(taskId)
        logI(TAG, "Deleting worktree for task $taskId at $path")
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
        if (Files.isDirectory(path)) {
            path.toFile().deleteRecursively()
        }
    }
}
