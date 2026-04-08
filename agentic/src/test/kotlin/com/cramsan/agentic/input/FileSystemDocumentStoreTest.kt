package com.cramsan.agentic.input

import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.defaultInputDocuments
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileSystemDocumentStoreTest {

    @TempDir
    lateinit var tempDir: Path

    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setUp() {
        Files.writeString(tempDir.resolve("goals-scope.md"), "# Goals & Scope\nSome content")
        Files.writeString(tempDir.resolve("architecture-design.md"), "# Architecture\nSome content")
        Files.writeString(tempDir.resolve("standards.md"), "# Standards\nSome content")
    }

    @Test
    fun `getAll returns 3 documents when all 3 input files are present`() {
        val store = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        val docs = store.getAll()
        assertEquals(3, docs.size, "Should return 3 documents")
    }

    @Test
    fun `updateStatus persists new status and is readable after fresh construction`() {
        val store = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        store.updateStatus("goals-scope", DocumentStatus.VALIDATED)

        // Simulate restart by creating a new store instance
        val freshStore = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        val doc = freshStore.get("goals-scope")
        assertEquals(DocumentStatus.VALIDATED, doc.status, "Status should be persisted and readable after restart")
    }

    @Test
    fun `onDocumentChanged resets all statuses to UNREVIEWED`() {
        val store = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        store.updateStatus("goals-scope", DocumentStatus.VALIDATED)
        store.updateStatus("architecture-design", DocumentStatus.NEEDS_REVISION)
        store.updateStatus("standards", DocumentStatus.IN_REVIEW)

        store.onDocumentChanged()

        for (doc in store.getAll()) {
            assertEquals(DocumentStatus.UNREVIEWED, doc.status, "All documents should be UNREVIEWED after onDocumentChanged()")
        }
    }

    @Test
    fun `allValidated returns false until all docs are VALIDATED then returns true`() {
        val store = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())

        assertFalse(store.allValidated(), "Should not be fully validated initially")

        store.updateStatus("goals-scope", DocumentStatus.VALIDATED)
        assertFalse(store.allValidated(), "Should not be fully validated with only 1 validated")

        store.updateStatus("architecture-design", DocumentStatus.VALIDATED)
        assertFalse(store.allValidated(), "Should not be fully validated with 2 out of 3 validated")

        store.updateStatus("standards", DocumentStatus.VALIDATED)
        assertTrue(store.allValidated(), "Should be fully validated when all 3 docs are VALIDATED")
    }

    @Test
    fun `onDocumentChanged persists reset to disk so fresh store also sees UNREVIEWED`() {
        val store = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        store.updateStatus("goals-scope", DocumentStatus.VALIDATED)
        store.onDocumentChanged()

        val freshStore = FileSystemDocumentStore(tempDir, json, defaultInputDocuments())
        val doc = freshStore.get("goals-scope")
        assertEquals(DocumentStatus.UNREVIEWED, doc.status, "Reset should be persisted to disk")
    }
}
