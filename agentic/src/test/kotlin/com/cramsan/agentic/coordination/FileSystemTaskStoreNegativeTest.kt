package com.cramsan.agentic.coordination

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Negative and edge-case tests for FileSystemTaskStore: empty files, malformed markdown,
 * missing required fields, non-existent file, and invalid timeout values.
 */
class FileSystemTaskStoreNegativeTest {

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun writeTaskList(content: String): FileSystemTaskStore {
        val file = tempDir.resolve("task-list.md")
        Files.writeString(file, content)
        return FileSystemTaskStore(file)
    }

    // ── Empty / no-match content ──────────────────────────────────────────────

    @Test
    fun `empty file throws IllegalStateException`() {
        val store = writeTaskList("")

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    @Test
    fun `file with only whitespace throws IllegalStateException`() {
        val store = writeTaskList("   \n\n   \t  \n")

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    @Test
    fun `file with prose but no task headers throws IllegalStateException`() {
        val store = writeTaskList(
            """
            # Task List

            This document describes what needs to be done.
            There are no task blocks yet.
            """.trimIndent(),
        )

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    // ── Missing required fields ───────────────────────────────────────────────

    @Test
    fun `task block missing Title field is not parsed`() {
        // Without a Title line the regex does not match → no tasks → IllegalStateException
        val store = writeTaskList(
            """
            ## Task: task-001
            Description: Something to do
            Dependencies: none
            """.trimIndent(),
        )

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    @Test
    fun `task block missing Description field is not parsed`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Do something
            Dependencies: none
            """.trimIndent(),
        )

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    @Test
    fun `task block missing Dependencies field is not parsed`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Do something
            Description: A description
            """.trimIndent(),
        )

        assertFailsWith<IllegalStateException> {
            store.getAll()
        }
    }

    // ── get() with invalid identifiers ───────────────────────────────────────

    @Test
    fun `get with empty string id throws IllegalArgumentException`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Only task
            Description: Test
            Dependencies: none
            """.trimIndent(),
        )

        assertFailsWith<IllegalArgumentException> {
            store.get("")
        }
    }

    @Test
    fun `get with blank id throws IllegalArgumentException`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Only task
            Description: Test
            Dependencies: none
            """.trimIndent(),
        )

        assertFailsWith<IllegalArgumentException> {
            store.get("   ")
        }
    }

    @Test
    fun `get with wrong case id throws IllegalArgumentException`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Only task
            Description: Test
            Dependencies: none
            """.trimIndent(),
        )

        // IDs are case-sensitive
        assertFailsWith<IllegalArgumentException> {
            store.get("TASK-001")
        }
    }

    // ── Non-existent file ─────────────────────────────────────────────────────

    @Test
    fun `non-existent file throws exception on getAll`() {
        val missingFile = tempDir.resolve("does-not-exist.md")
        val store = FileSystemTaskStore(missingFile)

        assertFailsWith<Exception> {
            store.getAll()
        }
    }

    // ── Invalid / boundary timeout values ────────────────────────────────────

    @Test
    fun `non-numeric timeout defaults to 3600`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Task
            Description: Test
            Dependencies: none
            Timeout: not-a-number
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(3600L, task.timeoutSeconds)
    }

    @Test
    fun `timeout with alphabetic suffix is partially parsed by greedy digit match`() {
        // The regex \d+ greedily matches the leading digits of "30m" → timeout becomes 30, NOT 3600.
        // This documents the current parsing behavior; callers should validate timeouts.
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Task
            Description: Test
            Dependencies: none
            Timeout: 30m
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(30L, task.timeoutSeconds)
    }

    @Test
    fun `timeout with purely alphabetic value defaults to 3600`() {
        // A purely non-numeric value (no leading digits) causes the optional Timeout group
        // to not match, so the field defaults to 3600.
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Task
            Description: Test
            Dependencies: none
            Timeout: abc
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(3600L, task.timeoutSeconds)
    }

    @Test
    fun `negative timeout defaults to 3600 because the regex only matches digits`() {
        // The regex uses \d+ which cannot match a leading minus sign.
        // The Timeout optional group fails to match "-1", so the default 3600 applies.
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: Task
            Description: Test
            Dependencies: none
            Timeout: -1
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(3600L, task.timeoutSeconds)
    }

    // ── Dependency edge cases ─────────────────────────────────────────────────

    @Test
    fun `dependency referencing non-existent task id is stored without validation`() {
        val store = writeTaskList(
            """
            ## Task: task-002
            Title: Second
            Description: Depends on a ghost
            Dependencies: task-ghost
            """.trimIndent(),
        )

        val task = store.getAll().single()
        // The store does not validate that referenced tasks exist
        assertEquals(listOf("task-ghost"), task.dependencies)
    }

    @Test
    fun `dependencies with extra whitespace around commas are trimmed`() {
        val store = writeTaskList(
            """
            ## Task: task-003
            Title: Third
            Description: Multi-dep
            Dependencies:  task-001 ,  task-002
            """.trimIndent(),
        )

        val task = store.getAll().single()
        assertEquals(listOf("task-001", "task-002"), task.dependencies)
    }

    @Test
    fun `duplicate task ids are all returned by getAll`() {
        val store = writeTaskList(
            """
            ## Task: task-001
            Title: First
            Description: First
            Dependencies: none

            ## Task: task-001
            Title: Duplicate
            Description: Also first
            Dependencies: none
            """.trimIndent(),
        )

        // The store does not deduplicate; both entries are returned
        val tasks = store.getAll()
        assertEquals(2, tasks.size)
        assertTrue(tasks.all { it.id == "task-001" })
    }
}
