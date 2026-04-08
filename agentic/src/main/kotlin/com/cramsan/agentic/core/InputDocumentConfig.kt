package com.cramsan.agentic.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for a user-defined input document.
 *
 * @property id Unique identifier for the document (e.g., "goals-scope")
 * @property filename Filename for the document (e.g., "goals-scope.md"). Can be relative to docsDir or absolute.
 * @property displayName Human-readable name (e.g., "Goals & Scope")
 * @property template Template content for scaffolding
 */
@Serializable
data class InputDocumentConfig(
    val id: String,
    val filename: String,
    val displayName: String,
    val template: DocumentTemplateConfig,
)

/**
 * Template configuration for scaffolding input documents.
 */
@Serializable
sealed class DocumentTemplateConfig {
    /**
     * Inline template content.
     */
    @Serializable
    @SerialName("inline")
    data class Inline(val content: String) : DocumentTemplateConfig()

    /**
     * File-based template. Path can be relative to docsDir or absolute.
     */
    @Serializable
    @SerialName("file")
    data class File(val path: String) : DocumentTemplateConfig()
}

/**
 * Returns the default input documents configuration matching the original hardcoded values.
 * Templates are stored in resources/templates/documents/ and referenced by path.
 */
fun defaultInputDocuments(): List<InputDocumentConfig> = listOf(
    InputDocumentConfig(
        id = "goals-scope",
        filename = "goals-scope.md",
        displayName = "Goals & Scope",
        template = DocumentTemplateConfig.File(path = "templates/documents/goals-scope.md"),
    ),
    InputDocumentConfig(
        id = "architecture-design",
        filename = "architecture-design.md",
        displayName = "Architecture & Design",
        template = DocumentTemplateConfig.File(path = "templates/documents/architecture-design.md"),
    ),
    InputDocumentConfig(
        id = "standards",
        filename = "standards.md",
        displayName = "Standards",
        template = DocumentTemplateConfig.File(path = "templates/documents/standards.md"),
    ),
)
