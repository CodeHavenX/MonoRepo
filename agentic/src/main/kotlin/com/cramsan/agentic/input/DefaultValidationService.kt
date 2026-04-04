package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.ValidationIssue
import com.cramsan.agentic.core.ValidationReport
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class DefaultValidationService(
    private val documentStore: DocumentStore,
    private val aiProvider: AiProvider,
    private val model: String,
    private val reviewerAgents: List<ReviewerAgent>,
    private val reviewerLoader: ReviewerLoader,
    private val json: Json,
    private val docsDir: Path,
) : ValidationService {

    override suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue> {
        val fileContent = Files.readString(docsDir.resolve(document.relativePath))

        val systemPrompt = """
            You are a document reviewer. Review the provided document and return a JSON array of ValidationIssue objects.
            Each issue should have these fields: id (string), documentId (string), description (string), severity ("BLOCKING" or "ADVISORY"), status ("OPEN").
            Return only a valid JSON array. If there are no issues, return an empty array [].
        """.trimIndent()

        val response = aiProvider.chat(
            model = model,
            systemPrompt = systemPrompt,
            messages = listOf(AiMessage("user", fileContent)),
            tools = emptyList(),
        )

        val textContent = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        val issues: List<ValidationIssue> = if (textContent != null) {
            json.decodeFromString(textContent.text)
        } else {
            emptyList()
        }

        val hasBlockingIssues = issues.any { it.severity == IssueSeverity.BLOCKING }
        val newStatus = if (hasBlockingIssues) DocumentStatus.NEEDS_REVISION else DocumentStatus.VALIDATED
        documentStore.updateStatus(document.id, newStatus)

        return issues
    }

    override suspend fun runValidationPass(): ValidationReport {
        val docs = documentStore.getAll()
        for (doc in docs) {
            documentStore.updateStatus(doc.id, DocumentStatus.IN_REVIEW)
        }

        val allIssues = mutableListOf<ValidationIssue>()
        for (doc in docs) {
            val issues = reviewDocument(doc)
            allIssues.addAll(issues)
        }

        val reviewerDefinitions = reviewerLoader.loadAll()
        if (reviewerDefinitions.isNotEmpty()) {
            coroutineScope {
                reviewerDefinitions.map { reviewerDef ->
                    reviewerAgents.map { agent ->
                        async { agent.reviewDocuments(reviewerDef, docs) }
                    }
                }.flatten().awaitAll()
            }.forEach { feedback ->
                println("=== Reviewer: ${feedback.reviewerName} ===\n${feedback.content}")
            }
        }

        val report = ValidationReport(
            runId = java.util.UUID.randomUUID().toString(),
            timestampEpochMs = System.currentTimeMillis(),
            issues = allIssues,
        )

        val reportContent = buildString {
            appendLine("# Validation Report")
            appendLine()
            appendLine("Run ID: ${report.runId}")
            appendLine("Timestamp: ${report.timestampEpochMs}")
            appendLine("Total Issues: ${report.issues.size}")
            appendLine()
            appendLine("```json")
            appendLine(json.encodeToString(ValidationReport.serializer(), report))
            appendLine("```")
        }

        Files.writeString(docsDir.resolve("validation-report.md"), reportContent)

        return report
    }
}
