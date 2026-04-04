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

class FileSystemTaskStoreTest {

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun writeTaskList(content: String): Path {
        val path = tempDir.resolve("task-list.md")
        Files.writeString(path, content)
        return path
    }

    @Test
    fun `parses single task with no dependencies`() {
        val content = """
## Task: task-001
Title: My First Task
Description: Do something useful
Dependencies: none
Timeout: 3600
""".trimIndent()
        val store = FileSystemTaskStore(writeTaskList(content))
        val tasks = store.getAll()
        assertEquals(1, tasks.size)
        val task = tasks[0]
        assertEquals("task-001", task.id)
        assertEquals("My First Task", task.title)
        assertEquals("Do something useful", task.description)
        assertTrue(task.dependencies.isEmpty())
        assertEquals(3600L, task.timeoutSeconds)
    }

    @Test
    fun `parses multiple tasks with dependencies`() {
        val content = """
## Task: task-001
Title: First Task
Description: Desc 1
Dependencies: none
Timeout: 3600

## Task: task-002
Title: Second Task
Description: Desc 2
Dependencies: task-001
Timeout: 7200

## Task: task-003
Title: Third Task
Description: Desc 3
Dependencies: task-001, task-002
Timeout: 1800
""".trimIndent()
        val store = FileSystemTaskStore(writeTaskList(content))
        val tasks = store.getAll()
        assertEquals(3, tasks.size)

        val task2 = store.get("task-002")
        assertEquals(listOf("task-001"), task2.dependencies)

        val task3 = store.get("task-003")
        assertEquals(listOf("task-001", "task-002"), task3.dependencies)
    }

    @Test
    fun `task with no dependencies has empty dependencies list`() {
        val content = """
## Task: standalone
Title: Standalone Task
Description: No deps
Dependencies: none
Timeout: 3600
""".trimIndent()
        val store = FileSystemTaskStore(writeTaskList(content))
        assertTrue(store.get("standalone").dependencies.isEmpty())
    }

    @Test
    fun `get with unknown id throws exception`() {
        val content = """
## Task: task-001
Title: Task
Description: Desc
Dependencies: none
Timeout: 3600
""".trimIndent()
        val store = FileSystemTaskStore(writeTaskList(content))
        assertFailsWith<IllegalArgumentException> {
            store.get("unknown-id")
        }
    }
}
