package com.cramsan.agentic.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AgenticDocumentTest {

    private val json = Json { prettyPrint = false }

    @Test
    fun `AgenticDocument round-trips through JSON`() {
        val original = AgenticDocument(
            id = "doc-001",
            typeId = "architecture-design",
            type = DocumentType.ARCHITECTURE_DESIGN,
            relativePath = "docs/architecture.md",
            status = DocumentStatus.UNREVIEWED,
            lastModifiedEpochMs = 1700000000000L,
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<AgenticDocument>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `DocumentType values round-trip through JSON`() {
        for (type in DocumentType.entries) {
            val encoded = json.encodeToString(type)
            val decoded = json.decodeFromString<DocumentType>(encoded)
            assertEquals(type, decoded)
        }
    }

    @Test
    fun `DocumentStatus values round-trip through JSON`() {
        for (status in DocumentStatus.entries) {
            val encoded = json.encodeToString(status)
            val decoded = json.decodeFromString<DocumentStatus>(encoded)
            assertEquals(status, decoded)
        }
    }

    @Test
    fun `AgenticDocument with all DocumentType and DocumentStatus combinations`() {
        val statuses = DocumentStatus.entries
        val types = DocumentType.entries

        for (type in types) {
            for (status in statuses) {
                val doc = AgenticDocument(
                    id = "doc-$type-$status",
                    typeId = type.name.lowercase().replace('_', '-'),
                    type = type,
                    relativePath = "docs/test.md",
                    status = status,
                    lastModifiedEpochMs = 0L,
                )
                val encoded = json.encodeToString(doc)
                val decoded = json.decodeFromString<AgenticDocument>(encoded)
                assertEquals(doc, decoded)
            }
        }
    }
}
