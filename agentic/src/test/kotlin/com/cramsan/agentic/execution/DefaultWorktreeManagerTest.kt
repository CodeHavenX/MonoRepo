package com.cramsan.agentic.execution

import com.cramsan.agentic.vcs.github.ShellResult
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultWorktreeManagerTest {

    @TempDir
    lateinit var repoRoot: Path

    @TempDir
    lateinit var agenticDir: Path

    private val shell = mockk<ShellRunner>()
    private lateinit var manager: DefaultWorktreeManager

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        manager = DefaultWorktreeManager(repoRoot, agenticDir, "main", shell)
    }

    @Test
    fun `getOrCreate calls git worktree add when directory does not exist`() {
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")

        val worktree = manager.getOrCreate("task-001")

        assertEquals("task-001", worktree.taskId)
        assertEquals("agentic/task-001", worktree.branchName)
        coVerify {
            shell.run(
                "git", "worktree", "add",
                "-b", "agentic/task-001",
                any(), // path
                "main",
            )
        }
    }

    @Test
    fun `getOrCreate does not call git worktree add when directory already exists`() {
        val worktreePath = agenticDir.resolve("worktrees/task-001")
        Files.createDirectories(worktreePath)

        val worktree = manager.getOrCreate("task-001")

        assertEquals("task-001", worktree.taskId)
        coVerify(exactly = 0) { shell.run(*anyVararg()) }
    }

    @Test
    fun `get returns null when directory does not exist`() {
        assertNull(manager.get("task-001"))
    }

    @Test
    fun `get returns Worktree when directory exists`() {
        val worktreePath = agenticDir.resolve("worktrees/task-001")
        Files.createDirectories(worktreePath)

        val worktree = manager.get("task-001")
        assertNotNull(worktree)
        assertEquals("task-001", worktree.taskId)
    }

    @Test
    fun `listAll returns all worktree subdirectories`() {
        Files.createDirectories(agenticDir.resolve("worktrees/task-001"))
        Files.createDirectories(agenticDir.resolve("worktrees/task-002"))

        val worktrees = manager.listAll()
        assertEquals(2, worktrees.size)
    }

    @Test
    fun `delete calls git worktree remove`() {
        val worktreePath = agenticDir.resolve("worktrees/task-001")
        Files.createDirectories(worktreePath)
        coEvery { shell.run(*anyVararg()) } returns ShellResult("", 0, "")

        manager.delete("task-001")

        coVerify {
            shell.run("git", "worktree", "remove", "--force", any())
        }
    }
}
