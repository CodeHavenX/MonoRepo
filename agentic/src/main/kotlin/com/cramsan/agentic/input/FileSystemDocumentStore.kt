package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.InputDocumentConfig
import com.cramsan.agentic.core.resolvePath
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

private const val TAG = "FileSystemDocumentStore"

/**
 * Filesystem-backed [DocumentStore] that persists document metadata as JSON sidecar files
 * alongside the actual document content in [docsDir].
 *
 * **Lazy loading**: document metadata is loaded from disk on the first call to [getAll] or [get]
 * and cached in memory for the lifetime of the instance. Subsequent calls use the in-memory
 * cache. This means external modifications to sidecar files after the first access are not
 * reflected without restarting the process.
 *
 * **Change detection**: [onDocumentChanged] resets all in-memory statuses to
 * [com.cramsan.agentic.core.DocumentStatus.UNREVIEWED] and persists the reset to disk. It does
 * not scan for new files — only the documents listed in [inputDocuments] at construction time
 * are tracked.
 *
 * **Content hashing**: document content is SHA-256 hashed when [updateStatus] transitions a
 * document to [com.cramsan.agentic.core.DocumentStatus.VALIDATED], and the hash is stored in the
 * sidecar. This hash is later used by [com.cramsan.agentic.input.DefaultWorkflowService] to
 * detect approval drift.
 */
class FileSystemDocumentStore(
    private val docsDir: Path,
    private val json: Json,
    private val inputDocuments: List<InputDocumentConfig>,
) : DocumentStore {

    @Serializable
    private data class DocumentMeta(
        val id: String,
        val status: DocumentStatus,
        val lastModifiedEpochMs: Long,
        val contentHash: String? = null,
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

        // Compute content hash when validating
        val contentHash = if (status == DocumentStatus.VALIDATED) {
            val filePath = resolvePath(docsDir, existing.relativePath)
            val content = Files.readString(filePath)
            computeContentHash(content).also {
                logD(TAG, "Computed content hash for $id: $it")
            }
        } else {
            null
        }
        writeSidecar(updated, contentHash)
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
        for (docConfig in inputDocuments) {
            val filePath = resolvePath(docsDir, docConfig.filename)
            logD(TAG, "Checking for document file: $filePath (id=${docConfig.id})")
            if (!Files.exists(filePath)) {
                logD(TAG, "Document file not found, skipping: $filePath")
                continue
            }

            val id = docConfig.id
            val lastModified = Files.getLastModifiedTime(filePath).toMillis()

            val sidecar = readSidecar(id)
            var status = sidecar?.status ?: DocumentStatus.UNREVIEWED
            if (sidecar == null) {
                logD(TAG, "No sidecar found for document $id; defaulting status to UNREVIEWED")
            }

            // Check if validated document has been modified
            if (status == DocumentStatus.VALIDATED && sidecar?.contentHash != null) {
                val currentContent = Files.readString(filePath)
                val currentHash = computeContentHash(currentContent)
                if (currentHash != sidecar.contentHash) {
                    logW(TAG, "Document $id content changed since validation (hash mismatch). Resetting to UNREVIEWED.")
                    logD(TAG, "Expected hash: ${sidecar.contentHash.take(RADIX)}..., actual: ${currentHash.take(RADIX)}...")
                    status = DocumentStatus.UNREVIEWED
                    // Update the sidecar to reflect the reset status
                    val resetDoc = AgenticDocument(
                        id = id,
                        typeId = docConfig.id,
                        relativePath = docConfig.filename,
                        status = status,
                        lastModifiedEpochMs = lastModified,
                    )
                    writeSidecar(resetDoc, null)
                } else {
                    logD(TAG, "Document $id hash verified, status remains VALIDATED")
                }
            }

            val doc = AgenticDocument(
                id = id,
                typeId = docConfig.id,
                relativePath = docConfig.filename,
                status = status,
                lastModifiedEpochMs = lastModified,
            )
            result[id] = doc
            logI(TAG, "Loaded document: id=$id, typeId=${docConfig.id}, status=$status, lastModified=$lastModified")
        }
        logI(TAG, "Document loading complete: ${result.size} document(s) loaded from $docsDir")
        return result
    }

    private fun sidecarPath(id: String): Path =
        docsDir.resolve(".agentic-meta").resolve("doc.$id.json")

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

    @Suppress("MagicNumber")
    private fun writeSidecar(document: AgenticDocument, contentHash: String? = null) {
        val path = sidecarPath(document.id)
        logD(TAG, "Writing sidecar for document ${document.id} to $path")
        Files.createDirectories(path.parent)
        val meta = DocumentMeta(
            id = document.id,
            status = document.status,
            lastModifiedEpochMs = document.lastModifiedEpochMs,
            contentHash = contentHash,
        )
        Files.writeString(path, json.encodeToString(DocumentMeta.serializer(), meta))
        logD(TAG, "Sidecar written for document ${document.id}: status=${document.status}, hash=${contentHash?.take(8)}...")
    }

    @Suppress("MagicNumber")
    private fun computeContentHash(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { (it.toInt() and 0xFF).toString(RADIX).padStart(2, '0') }
    }

}
private const val RADIX = 16
