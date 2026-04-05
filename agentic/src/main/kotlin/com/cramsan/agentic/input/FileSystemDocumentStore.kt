package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.DocumentType
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "FileSystemDocumentStore"

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

    override fun getAll(): List<AgenticDocument> {
        val all = documents.values.toList()
        logD(TAG, "getAll: returning ${all.size} documents")
        return all
    }

    override fun get(id: String): AgenticDocument {
        logD(TAG, "get: id=$id")
        return documents[id] ?: throw IllegalArgumentException("Unknown document id: $id")
    }

    override fun updateStatus(id: String, status: DocumentStatus) {
        logD(TAG, "updateStatus called: id=$id, newStatus=$status")
        val existing = documents[id] ?: throw IllegalArgumentException("Unknown document id: $id")
        logI(TAG, "Document $id status: ${existing.status} → $status")
        val updated = existing.copy(status = status)
        documents[id] = updated
        writeSidecar(updated)
    }

    override fun onDocumentChanged() {
        val keys = documents.keys.toList()
        logI(TAG, "onDocumentChanged: resetting ${keys.size} documents to UNREVIEWED")
        for (key in keys) {
            val doc = documents[key] ?: continue
            logD(TAG, "Resetting document $key from ${doc.status} to UNREVIEWED")
            val reset = doc.copy(status = DocumentStatus.UNREVIEWED)
            documents[key] = reset
            writeSidecar(reset)
        }
    }

    override fun allValidated(): Boolean {
        val result = documents.values.all { it.status == DocumentStatus.VALIDATED }
        logD(TAG, "allValidated: $result (${documents.size} documents checked)")
        return result
    }

    private fun loadFromDisk(): MutableMap<String, AgenticDocument> {
        logI(TAG, "Loading documents from disk: docsDir=$docsDir")
        val result = mutableMapOf<String, AgenticDocument>()
        for ((filename, type) in FILE_MAP) {
            val filePath = docsDir.resolve(filename)
            logD(TAG, "Checking for document file: $filePath (type=$type)")
            if (!Files.exists(filePath)) {
                logD(TAG, "Document file not found, skipping: $filePath")
                continue
            }

            val id = filename.removeSuffix(".md")
            val lastModified = Files.getLastModifiedTime(filePath).toMillis()

            val sidecar = readSidecar(id)
            val status = sidecar?.status ?: DocumentStatus.UNREVIEWED
            if (sidecar == null) {
                logD(TAG, "No sidecar found for document $id; defaulting status to UNREVIEWED")
            }

            val doc = AgenticDocument(
                id = id,
                type = type,
                relativePath = filename,
                status = status,
                lastModifiedEpochMs = lastModified,
            )
            result[id] = doc
            logI(TAG, "Loaded document: id=$id, type=$type, status=$status, lastModified=$lastModified")
        }
        logI(TAG, "Document loading complete: ${result.size} document(s) loaded from $docsDir")
        return result
    }

    private fun sidecarPath(id: String): Path =
        docsDir.resolve(".agentic-meta").resolve("$id.json")

    private fun readSidecar(id: String): DocumentMeta? {
        val path = sidecarPath(id)
        logD(TAG, "Reading sidecar for id=$id at $path")
        if (!Files.exists(path)) {
            logD(TAG, "Sidecar not found for id=$id")
            return null
        }
        return try {
            val meta = json.decodeFromString<DocumentMeta>(Files.readString(path))
            logD(TAG, "Sidecar loaded for id=$id: status=${meta.status}")
            meta
        } catch (e: Exception) {
            logW(TAG, "Failed to parse sidecar for id=$id at $path", e)
            null
        }
    }

    private fun writeSidecar(document: AgenticDocument) {
        val path = sidecarPath(document.id)
        logD(TAG, "Writing sidecar for document ${document.id} to $path")
        Files.createDirectories(path.parent)
        val meta = DocumentMeta(
            id = document.id,
            status = document.status,
            lastModifiedEpochMs = document.lastModifiedEpochMs,
        )
        Files.writeString(path, json.encodeToString(DocumentMeta.serializer(), meta))
        logD(TAG, "Sidecar written for document ${document.id}: status=${document.status}")
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
