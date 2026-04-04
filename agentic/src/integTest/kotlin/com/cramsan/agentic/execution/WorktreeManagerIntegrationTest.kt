package com.cramsan.agentic.execution

import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Tag("requires-git")
class WorktreeManagerIntegrationTest {

    companion object {
        @TempDir
        @JvmStatic
        lateinit var repoRoot: Path

        @BeforeAll
        @JvmStatic
        fun initGitRepo() {
            EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
            val shell = ShellRunner()
            runBlocking {
                shell.run("git", "init", repoRoot.toString())
                shell.run("git", "-C", repoRoot.toString(), "config", "user.email", "test@test.com")
                shell.run("git", "-C", repoRoot.toString(), "config", "user.name", "Test User")
                shell.run("git", "-C", repoRoot.toString(), "commit", "--allow-empty", "-m", "init")
            }
        }
    }

    @TempDir
    lateinit var agenticDir: Path

    private lateinit var manager: DefaultWorktreeManager

    @BeforeEach
    fun setup() {
        manager = DefaultWorktreeManager(repoRoot, agenticDir, "main", ShellRunner())
    }

    @Test
    fun `getOrCreate creates a git worktree directory at expected path`() {
        val worktree = manager.getOrCreate("task-001")

        assertNotNull(worktree)
        assertEquals("task-001", worktree.taskId)
        assertEquals("agentic/task-001", worktree.branchName)
        assertTrue(Files.isDirectory(worktree.path), "Worktree directory should exist")
    }

    @Test
    fun `getOrCreate is idempotent - second call does not fail`() {
        manager.getOrCreate("task-002")
        val second = manager.getOrCreate("task-002") // should not throw

        assertNotNull(second)
    }

    @Test
    fun `listAll returns the correct number of worktrees`() {
        manager.getOrCreate("task-003")
        manager.getOrCreate("task-004")

        val list = manager.listAll()
        assertTrue(list.size >= 2, "Expected at least 2 worktrees, got ${list.size}")
    }

    @Test
    fun `delete removes worktree directory`() {
        manager.getOrCreate("task-005")
        assertTrue(Files.isDirectory(agenticDir.resolve("worktrees/task-005")))

        manager.delete("task-005")

        assertNull(manager.get("task-005"), "Worktree should be gone after delete")
    }
}
