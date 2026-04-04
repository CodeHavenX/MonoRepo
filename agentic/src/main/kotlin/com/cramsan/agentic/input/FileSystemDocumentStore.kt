package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class FileSystemDocumentStore(
    private val docsDir: Path,
    private val json: Json,
) : DocumentStore {

    @Serializable
    private data class DocumentMeta(
        val id: String,
        val status: DocumentStatus,
        val lastModifiedEpochMs: Long,
    )

    private val documents: MutableMap<String, AgenticDocument> by lazy { loadFromDisk() }

    override fun getAll(): List<AgenticDocument> = documents.values.toList()

    override fun get(id: String): AgenticDocument =
        documents[id] ?: throw IllegalArgumentException("Unknown document id: $id")

    override fun updateStatus(id: String, status: DocumentStatus) {
        val existing = documents[id] ?: throw IllegalArgumentException("Unknown document id: $id")
        val updated = existing.copy(status = status)
        documents[id] = updated
        writeSidecar(updated)
    }

    override fun onDocumentChanged() {
        val keys = documents.keys.toList()
        for (key in keys) {
            val doc = documents[key] ?: continue
            val reset = doc.copy(status = DocumentStatus.UNREVIEWED)
            documents[key] = reset
            writeSidecar(reset)
        }
    }

    override fun allValidated(): Boolean = documents.values.all { it.status == DocumentStatus.VALIDATED }

    private fun loadFromDisk(): MutableMap<String, AgenticDocument> {
        val result = mutableMapOf<String, AgenticDocument>()
        for ((filename, type) in FILE_MAP) {
            val filePath = docsDir.resolve(filename)
            if (!Files.exists(filePath)) continue

            val id = filename.removeSuffix(".md")
            val lastModified = Files.getLastModifiedTime(filePath).toMillis()

            val sidecar = readSidecar(id)
            val status = sidecar?.status ?: DocumentStatus.UNREVIEWED

            result[id] = AgenticDocument(
                id = id,
                type = type,
                relativePath = filename,
                status = status,
                lastModifiedEpochMs = lastModified,
            )
        }
        return result
    }

    private fun sidecarPath(id: String): Path =
        docsDir.resolve(".agentic-meta").resolve("$id.json")

    private fun readSidecar(id: String): DocumentMeta? {
        val path = sidecarPath(id)
        if (!Files.exists(path)) return null
        return try {
            json.decodeFromString<DocumentMeta>(Files.readString(path))
        } catch (e: Exception) {
            null
        }
    }

    private fun writeSidecar(document: AgenticDocument) {
        val path = sidecarPath(document.id)
        Files.createDirectories(path.parent)
        val meta = DocumentMeta(
            id = document.id,
            status = document.status,
            lastModifiedEpochMs = document.lastModifiedEpochMs,
        )
        Files.writeString(path, json.encodeToString(DocumentMeta.serializer(), meta))
    }

    companion object {
        private val FILE_MAP = linkedMapOf(
            "goals-scope.md" to DocumentType.GOALS_SCOPE,
            "architecture-design.md" to DocumentType.ARCHITECTURE_DESIGN,
            "standards.md" to DocumentType.STANDARDS,
            "task-list.md" to DocumentType.TASK_LIST,
        )
    }
}
