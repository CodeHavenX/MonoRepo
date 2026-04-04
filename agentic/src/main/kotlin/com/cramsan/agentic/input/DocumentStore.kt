package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus

interface DocumentStore {
    fun getAll(): List<AgenticDocument>
    fun get(id: String): AgenticDocument
    fun updateStatus(id: String, status: DocumentStatus)
    fun onDocumentChanged()
    fun allValidated(): Boolean
}
