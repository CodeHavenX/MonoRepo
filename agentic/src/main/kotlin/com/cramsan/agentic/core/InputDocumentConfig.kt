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
 */
fun defaultInputDocuments(): List<InputDocumentConfig> = listOf(
    InputDocumentConfig(
        id = "goals-scope",
        filename = "goals-scope.md",
        displayName = "Goals & Scope",
        template = DocumentTemplateConfig.Inline(
            content = """
                # Goals & Scope

                ## Goals & Scope

                Describe the high-level goals and scope of this project.

                ### Goals

                - Goal 1: ...
                - Goal 2: ...

                ### Out of Scope

                - Item 1: ...
            """.trimIndent()
        ),
    ),
    InputDocumentConfig(
        id = "architecture-design",
        filename = "architecture-design.md",
        displayName = "Architecture & Design",
        template = DocumentTemplateConfig.Inline(
            content = """
                # Architecture & Design

                ## Architecture & Design

                Describe the system architecture and key design decisions.

                ### Components

                - Component 1: ...
                - Component 2: ...

                ### Data Flow

                Describe how data flows through the system.
            """.trimIndent()
        ),
    ),
    InputDocumentConfig(
        id = "standards",
        filename = "standards.md",
        displayName = "Standards",
        template = DocumentTemplateConfig.Inline(
            content = """
                # Standards

                ## Standards

                Define the coding standards, conventions, and best practices for this project.

                ### Coding Style

                - Use meaningful names
                - Write self-documenting code

                ### Testing

                - Unit tests required for all business logic
                - Integration tests for external dependencies
            """.trimIndent()
        ),
    ),
)
