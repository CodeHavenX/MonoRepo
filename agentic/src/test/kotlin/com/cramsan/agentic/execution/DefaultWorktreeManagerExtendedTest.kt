package com.cramsan.agentic.execution

import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Extended tests for DefaultWorktreeManager enforcing the worktree lifecycle and
 * branch naming conventions from TECH_DESIGN.md §5.3 and ARCHITECTURE.md §3.1.
 */
class DefaultWorktreeManagerExtendedTest {

    @TempDir
    lateinit var repoRoot: Path

    @TempDir
    lateinit var agenticDir: Path

    private val shell = mockk<ShellRunner>()

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun makeManager(baseBranch: String = "main") =
        DefaultWorktreeManager(repoRoot, agenticDir, baseBranch, shell)

    // ── Branch naming convention ──────────────────────────────────────────────

    @Test
    fun `getOrCreate uses branch name agentic-taskId`() = runTest {
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val manager = makeManager()
        val worktree = manager.getOrCreate("task-123")

        assertEquals("agentic/task-123", worktree.branchName)
    }

    @Test
    fun `getOrCreate stores worktree under agenticDir-worktrees-taskId`() = runTest {
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val manager = makeManager()
        val worktree = manager.getOrCreate("task-abc")

        val expectedPath = agenticDir.resolve("worktrees/task-abc")
        assertEquals(expectedPath, worktree.path)
    }

    // ── Lazy creation ─────────────────────────────────────────────────────────

    @Test
    fun `get returns null when worktree directory does not exist`() = runTest {
        val manager = makeManager()

        val result = manager.get("nonexistent-task")

        assertNull(result)
    }

    @Test
    fun `get returns worktree when directory exists on disk`() = runTest {
        val manager = makeManager()
        val worktreePath = agenticDir.resolve("worktrees/existing-task")
        Files.createDirectories(worktreePath)

        val result = manager.get("existing-task")

        assertNotNull(result)
        assertEquals("existing-task", result.taskId)
        assertEquals(worktreePath, result.path)
    }

    // ── getOrCreate: returns existing worktree without running git ────────────

    @Test
    fun `getOrCreate does not call git when worktree directory already exists`() = runTest {
        val manager = makeManager()
        val worktreePath = agenticDir.resolve("worktrees/task-exists")
        Files.createDirectories(worktreePath)

        manager.getOrCreate("task-exists")

        coVerify(exactly = 0) { shell.run(*anyVararg()) }
    }

    @Test
    fun `getOrCreate calls git worktree add when worktree does not exist`() = runTest {
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val manager = makeManager()
        manager.getOrCreate("new-task")

        coVerify {
            shell.run(
                "git", "worktree", "add",
                any(), // path
                any(), // -b or HEAD
                any(), // branch name or base branch
                *anyVararg(),
                workingDir = any(),
            )
        }
    }

    // ── listAll ────────────────────────────────────────────────────────────────

    @Test
    fun `listAll returns empty list when worktrees directory does not exist`() {
        val manager = makeManager()
        val result = manager.listAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `listAll returns one Worktree per subdirectory in worktrees dir`() = runTest {
        val manager = makeManager()
        Files.createDirectories(agenticDir.resolve("worktrees/task-a"))
        Files.createDirectories(agenticDir.resolve("worktrees/task-b"))
        Files.createDirectories(agenticDir.resolve("worktrees/task-c"))

        val result = manager.listAll()

        assertEquals(3, result.size)
        val ids = result.map { it.taskId }.toSet()
        assertTrue("task-a" in ids)
        assertTrue("task-b" in ids)
        assertTrue("task-c" in ids)
    }

    @Test
    fun `listAll ignores files (only directories are worktrees)`() {
        val manager = makeManager()
        Files.createDirectories(agenticDir.resolve("worktrees"))
        Files.writeString(agenticDir.resolve("worktrees/not-a-worktree.txt"), "file")
        Files.createDirectories(agenticDir.resolve("worktrees/real-task"))

        val result = manager.listAll()

        assertEquals(1, result.size)
        assertEquals("real-task", result.single().taskId)
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Test
    fun `delete invokes git worktree remove`() = runTest {
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")
        Files.createDirectories(agenticDir.resolve("worktrees/task-to-delete"))

        val manager = makeManager()
        manager.delete("task-to-delete")

        coVerify {
            shell.run(
                "git",
                "worktree",
                "remove",
                any(),
                *anyVararg(),
                workingDir = any(),
            )
        }
    }

    // ── worktree path is consistent across getOrCreate and get ────────────────

    @Test
    fun `worktree path returned by getOrCreate matches path returned by get`() = runTest {
        coEvery { shell.run(*anyVararg(), workingDir = any()) } returns ShellResult("", 0, "")

        val manager = makeManager()
        val created = manager.getOrCreate("task-xyz")

        // Simulate what git worktree add would have done: create the directory on disk
        Files.createDirectories(created.path)

        val retrieved = manager.get("task-xyz")

        assertNotNull(retrieved)
        assertEquals(created.path, retrieved.path)
        assertEquals(created.taskId, retrieved.taskId)
    }
}
