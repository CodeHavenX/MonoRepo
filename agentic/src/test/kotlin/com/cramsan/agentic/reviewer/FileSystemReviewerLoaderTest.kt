package com.cramsan.agentic.reviewer

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileSystemReviewerLoaderTest {

    @TempDir
    lateinit var reviewersDir: Path

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @Test
    fun `loadAll returns one ReviewerDefinition per md file`() {
        Files.writeString(reviewersDir.resolve("security.md"), "You are a security reviewer")
        Files.writeString(reviewersDir.resolve("performance.md"), "You are a performance reviewer")

        val loader = FileSystemReviewerLoader(reviewersDir)
        val defs = loader.loadAll()

        assertEquals(2, defs.size)
    }

    @Test
    fun `name field is filename without md extension`() {
        Files.writeString(reviewersDir.resolve("design-patterns.md"), "You are a design patterns reviewer")

        val loader = FileSystemReviewerLoader(reviewersDir)
        val defs = loader.loadAll()

        assertEquals(1, defs.size)
        assertEquals("design-patterns", defs[0].name)
    }

    @Test
    fun `systemPrompt field contains full file content`() {
        val content = "You are a security reviewer.\nCheck for SQL injection and XSS."
        Files.writeString(reviewersDir.resolve("security.md"), content)

        val loader = FileSystemReviewerLoader(reviewersDir)
        val defs = loader.loadAll()

        assertEquals(content, defs[0].systemPrompt)
    }

    @Test
    fun `empty directory returns empty list`() {
        val loader = FileSystemReviewerLoader(reviewersDir)
        assertTrue(loader.loadAll().isEmpty())
    }

    @Test
    fun `non-existent directory returns empty list`() {
        val nonExistentDir = reviewersDir.resolve("doesnt-exist")
        val loader = FileSystemReviewerLoader(nonExistentDir)
        assertTrue(loader.loadAll().isEmpty())
    }

    @Test
    fun `non-md files are ignored`() {
        Files.writeString(reviewersDir.resolve("security.md"), "Security reviewer")
        Files.writeString(reviewersDir.resolve("notes.txt"), "Not a reviewer")

        val loader = FileSystemReviewerLoader(reviewersDir)
        val defs = loader.loadAll()

        assertEquals(1, defs.size)
        assertEquals("security", defs[0].name)
    }
}
