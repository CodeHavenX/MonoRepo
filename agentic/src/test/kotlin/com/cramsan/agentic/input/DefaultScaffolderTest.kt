package com.cramsan.agentic.input

import com.cramsan.agentic.core.defaultInputDocuments
import com.cramsan.agentic.core.defaultReviewers
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

class DefaultScaffolderTest {

    @TempDir
    lateinit var tempDir: Path

    private val scaffolder = DefaultScaffolder(defaultInputDocuments(), defaultReviewers())

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @Test
    fun `scaffold creates all 5 expected files`() {
        scaffolder.scaffold(tempDir)

        assertTrue(Files.exists(tempDir.resolve("goals-scope.md")), "goals-scope.md should exist")
        assertTrue(Files.exists(tempDir.resolve("architecture-design.md")), "architecture-design.md should exist")
        assertTrue(Files.exists(tempDir.resolve("standards.md")), "standards.md should exist")
        assertTrue(Files.exists(tempDir.resolve("reviewers/security.md")), "reviewers/security.md should exist")
        assertTrue(Files.exists(tempDir.resolve("reviewers/design-patterns.md")), "reviewers/design-patterns.md should exist")
    }

    @Test
    fun `scaffold does not create task-list md`() {
        scaffolder.scaffold(tempDir)
        assertTrue(!Files.exists(tempDir.resolve("task-list.md")), "task-list.md should not be scaffolded (it is AI-generated)")
    }

    @Test
    fun `scaffold creates files with non-empty content and expected markers`() {
        scaffolder.scaffold(tempDir)

        val goalsContent = Files.readString(tempDir.resolve("goals-scope.md"))
        assertTrue(goalsContent.isNotEmpty(), "goals-scope.md should be non-empty")
        assertTrue(goalsContent.contains("## Goals & Scope"), "goals-scope.md should contain '## Goals & Scope'")

        val architectureContent = Files.readString(tempDir.resolve("architecture-design.md"))
        assertTrue(architectureContent.isNotEmpty(), "architecture-design.md should be non-empty")
        assertTrue(architectureContent.contains("## Architecture & Design"), "architecture-design.md should contain '## Architecture & Design'")

        val standardsContent = Files.readString(tempDir.resolve("standards.md"))
        assertTrue(standardsContent.isNotEmpty(), "standards.md should be non-empty")
        assertTrue(standardsContent.contains("## Standards"), "standards.md should contain '## Standards'")

        val securityContent = Files.readString(tempDir.resolve("reviewers/security.md"))
        assertTrue(securityContent.isNotEmpty(), "reviewers/security.md should be non-empty")

        val designPatternsContent = Files.readString(tempDir.resolve("reviewers/design-patterns.md"))
        assertTrue(designPatternsContent.isNotEmpty(), "reviewers/design-patterns.md should be non-empty")
    }

    @Test
    fun `scaffold is idempotent and does not overwrite existing files`() {
        scaffolder.scaffold(tempDir)

        val originalGoalsContent = Files.readString(tempDir.resolve("goals-scope.md"))

        // Overwrite with custom content to detect if scaffold would overwrite
        val customContent = "Custom content that should not be overwritten"
        Files.writeString(tempDir.resolve("goals-scope.md"), customContent)

        // Second scaffold call should not throw and should not overwrite existing files
        scaffolder.scaffold(tempDir)

        val afterSecondScaffold = Files.readString(tempDir.resolve("goals-scope.md"))
        assertEquals(customContent, afterSecondScaffold, "Existing file should not be overwritten by scaffold")
    }

    @Test
    fun `scaffold does not throw when called twice`() {
        scaffolder.scaffold(tempDir)
        // Should not throw
        scaffolder.scaffold(tempDir)
    }
}
