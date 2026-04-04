package com.cramsan.agentic.coordination

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Extended tests for FileSystemTaskStore covering timeout parsing, edge-case dependency
 * formats, and parsing robustness requirements from TECH_DESIGN.md §4.2.
 */
class FileSystemTaskStoreExtendedTest {

    @TempDir
    lateinit var tempDir: Path

    private fun writeTaskList(content: String): FileSystemTaskStore {
        val file = tempDir.resolve("task-list.md")
        Files.writeString(file, content)
        return FileSystemTaskStore(file)
    }

    // ── Timeout field parsing ─────────────────────────────────────────────────

    @Test
    fun `task with explicit timeout is parsed correctly`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Do something
            Description: A task with custom timeout
            Dependencies: none
            Timeout: 7200
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(7200L, task.timeoutSeconds)
    }

    @Test
    fun `task without timeout field uses default of 3600 seconds`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Do something
            Description: No timeout specified
            Dependencies: none
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(3600L, task.timeoutSeconds)
    }

    @Test
    fun `task with timeout 0 parses as zero`() {
        val store = writeTaskList(
            """
            ## Task: task-zero-timeout
            Title: Zero timeout
            Description: Test
            Dependencies: none
            Timeout: 0
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(0L, task.timeoutSeconds)
    }

    // ── Dependency parsing ────────────────────────────────────────────────────

    @Test
    fun `task with single dependency is parsed correctly`() {
        val store = writeTaskList(
            """
            ## Task: task-002
            Title: Second task
            Description: Depends on first
            Dependencies: task-001
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(listOf("task-001"), task.dependencies)
    }

    @Test
    fun `task with multiple dependencies separated by commas`() {
        val store = writeTaskList(
            """
            ## Task: task-003
            Title: Third task
            Description: Depends on two tasks
            Dependencies: task-001, task-002
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(listOf("task-001", "task-002"), task.dependencies)
    }

    @Test
    fun `task with dependencies none has empty list`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: First
            Description: No deps
            Dependencies: none
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertTrue(task.dependencies.isEmpty())
    }

    // ── Multiple tasks ─────────────────────────────────────────────────────────

    @Test
    fun `multiple tasks are all parsed from the file`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: First task
            Description: First
            Dependencies: none

            ## Task: task-002
            Title: Second task
            Description: Second
            Dependencies: task-001

            ## Task: task-003
            Title: Third task
            Description: Third
            Dependencies: task-001, task-002
            """.trimIndent(),
        )

        val tasks = store.getAll()
        assertEquals(3, tasks.size)
        val ids = tasks.map { it.id }.toSet()
        assertTrue("task-001" in ids)
        assertTrue("task-002" in ids)
        assertTrue("task-003" in ids)
    }

    @Test
    fun `get by id returns correct task`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: First
            Description: Desc 1
            Dependencies: none

            ## Task: task-002
            Title: Second
            Description: Desc 2
            Dependencies: none
            """.trimIndent(),
        )

        val task = store.get("task-002")
        assertEquals("Second", task.title)
        assertEquals("Desc 2", task.description)
    }

    @Test
    fun `get with unknown id throws exception`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Only task
            Description: Test
            Dependencies: none
            """.trimIndent(),
        )

        assertFailsWith<Exception> {
            store.get("nonexistent-id")
        }
    }

    // ── Task id and title correctness ─────────────────────────────────────────

    @Test
    fun `task id is taken from the Task header line`() {
        val store = writeTaskList(
            """
            ## Task: my-feature-task
            Title: My Feature
            Description: Implement feature X
            Dependencies: none
            """.trimIndent(),
        )

        assertEquals("my-feature-task", store.getAll().single().id)
    }

    @Test
    fun `task title is taken from the Title field`() {
        val store = writeTaskList(
            """
            ## Task: t1
            Title: Build the rocket engine
            Description: Implement combustion chamber
            Dependencies: none
            """.trimIndent(),
        )

        assertEquals("Build the rocket engine", store.getAll().single().title)
    }

    @Test
    fun `task description is taken from the Description field`() {
        val store = writeTaskList(
            """
            ## Task: t1
            Title: Title
            Description: The full description goes here with multiple words.
            Dependencies: none
            """.trimIndent(),
        )

        assertEquals("The full description goes here with multiple words.", store.getAll().single().description)
    }
}
