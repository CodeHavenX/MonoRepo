package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus

/**
 * Tracks the validation lifecycle of planning documents used during the `agentic plan` phase.
 * Documents are loaded from the docs directory and their metadata (status, last-modified time)
 * is persisted alongside the content files.
 *
 * **Change propagation**: [onDocumentChanged] resets ALL documents back to
 * [com.cramsan.agentic.core.DocumentStatus.UNREVIEWED]. This is intentional — when any input
 * changes, all documents must be re-validated because the AI's evaluation of one document may
 * depend on the content of another.
 *
 * [get] throws if the document ID is not found; callers must ensure IDs are valid.
 * [getAll] always returns the complete set of tracked documents.
 */
interface DocumentStore {
    /** Returns all tracked planning documents in unspecified order. */
    fun getAll(): List<AgenticDocument>

    /** Returns the document with the given [id]. Throws [NoSuchElementException] if not found. */
    fun get(id: String): AgenticDocument

    /** Updates the persisted [status] of the document with the given [id]. */
    fun updateStatus(id: String, status: DocumentStatus)

    /**
     * Marks all tracked documents as [com.cramsan.agentic.core.DocumentStatus.UNREVIEWED].
     * Called when any document file is modified on disk to force a full re-validation pass.
     */
    fun onDocumentChanged()

    /** Returns true only if every tracked document has reached [com.cramsan.agentic.core.DocumentStatus.VALIDATED]. */
    fun allValidated(): Boolean
}
