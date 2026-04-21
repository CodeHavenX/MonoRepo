package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.DocumentStatus
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.ValidationIssue
import com.cramsan.agentic.core.ValidationReport
import com.cramsan.agentic.core.resolvePath
import com.cramsan.agentic.reviewer.ReviewerAgent
import com.cramsan.agentic.reviewer.ReviewerLoader
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultValidationService"
private const val MAX_LOG_PREVIEW_LENGTH = 200

/**
 * Production [ValidationService] that uses an AI model to evaluate planning documents for
 * quality and completeness during the `agentic plan` phase.
 *
 * **AI response parsing**: the AI is prompted to return a JSON array of issues. The response
 * is parsed leniently — text before the first `[` and after the last `]` is stripped to handle
 * models that add explanatory prose around the JSON. Parse failures fall back to an empty
 * issue list with a warning log rather than throwing.
 *
 * **Reviewer pipeline**: after the primary validation pass, all [ReviewerAgent]s are invoked
 * in parallel for each [com.cramsan.agentic.core.ReviewerDefinition] returned by [ReviewerLoader].
 * Reviewer feedback is printed to the console but does not affect document status or the
 * validation report — it is purely advisory output.
 *
 * **Report persistence**: the [com.cramsan.agentic.core.ValidationReport] is serialised as JSON
 * and written to `docs/validation-report.md`. A new run overwrites the previous report.
 */
class DefaultValidationService(
    private val documentStore: DocumentStore,
    private val aiProvider: AiProvider,
    private val reviewerAgents: List<ReviewerAgent>,
    private val reviewerLoader: ReviewerLoader,
    private val json: Json,
    private val docsDir: Path,
) : ValidationService {

    override suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue> {
        logI(TAG, "Starting review for document id=${document.id}, path=${document.relativePath}, typeId=${document.typeId}")
        val fileContent = Files.readString(resolvePath(docsDir, document.relativePath))
        logD(TAG, "Document ${document.id} content length: ${fileContent.length} chars")

        val systemPrompt = """
            You are a document reviewer. Your response must be a raw JSON array and nothing else.
            Do not include any explanation, preamble, or markdown formatting.
            Review the provided document and return a JSON array of ValidationIssue objects.
            Each object must have exactly these fields:
              - id: a unique string identifier for the issue
              - documentId: the document identifier (use "${document.id}")
              - description: a clear description of the issue
              - severity: either "BLOCKING" or "ADVISORY"
              - status: always "OPEN"
            If there are no issues, return an empty JSON array: []
            Your entire response must start with '[' and end with ']'. No other text is allowed.
        """.trimIndent()

        logD(TAG, "Invoking AI reviewer for document ${document.id}")
        val response = aiProvider.chat(
            systemPrompt = systemPrompt,
            messages = listOf(AiMessage("user", fileContent)),
            tools = emptyList(),
        )

        val textContent = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()
        if (textContent == null) {
            logW(TAG, "AI response for document ${document.id} contained no text content; returning empty issue list")
        } else {
            logD(TAG, "AI response for document ${document.id} text length: ${textContent.text.length} chars")
        }

        val issues: List<ValidationIssue> = if (textContent != null) {
            val rawText = textContent.text.trim()
            val startIdx = rawText.indexOf('[')
            val endIdx = rawText.lastIndexOf(']')
            if (startIdx == -1 || endIdx == -1 || startIdx > endIdx) {
                logW(TAG, "AI response for document ${document.id} did not contain a JSON array; returning empty issue list. Response: ${rawText.take(MAX_LOG_PREVIEW_LENGTH)}")
                emptyList()
            } else {
                val rawJson = rawText.substring(startIdx, endIdx + 1)
                try {
                    json.decodeFromString(rawJson)
                } catch (e: Exception) {
                    logW(TAG, "Failed to parse AI response as JSON for document ${document.id}: ${e.message}. Raw JSON: ${rawJson.take(MAX_LOG_PREVIEW_LENGTH)}")
                    emptyList()
                }
            }
        } else {
            emptyList()
        }

        val blockingCount = issues.count { it.severity == IssueSeverity.BLOCKING }
        val advisoryCount = issues.count { it.severity == IssueSeverity.ADVISORY }
        logI(TAG, "Document ${document.id} review complete: ${issues.size} issues (blocking=$blockingCount, advisory=$advisoryCount)")

        val hasBlockingIssues = blockingCount > 0
        val newStatus = if (hasBlockingIssues) DocumentStatus.NEEDS_REVISION else DocumentStatus.VALIDATED
        logI(TAG, "Document ${document.id} status transition: ${document.status} → $newStatus")
        documentStore.updateStatus(document.id, newStatus)

        return issues
    }

    override suspend fun runValidationPass(): ValidationReport {
        val docs = documentStore.getAll()
        logI(TAG, "Starting validation pass for ${docs.size} documents")

        for (doc in docs) {
            logD(TAG, "Marking document ${doc.id} as IN_REVIEW")
            documentStore.updateStatus(doc.id, DocumentStatus.IN_REVIEW)
        }

        val allIssues = mutableListOf<ValidationIssue>()
        for (doc in docs) {
            logD(TAG, "Reviewing document: ${doc.id}")
            val issues = reviewDocument(doc)
            allIssues.addAll(issues)
        }
        logI(TAG, "Per-document review complete: ${allIssues.size} total issues across ${docs.size} documents")

        val reviewerDefinitions = reviewerLoader.loadAll()
        logI(TAG, "Loaded ${reviewerDefinitions.size} reviewer definitions; ${reviewerAgents.size} reviewer agent(s) available")
        if (reviewerDefinitions.isNotEmpty()) {
            logD(TAG, "Dispatching ${reviewerDefinitions.size * reviewerAgents.size} async reviewer agent invocations")
            coroutineScope {
                reviewerDefinitions.map { reviewerDef ->
                    reviewerAgents.map { agent ->
                        logD(TAG, "Invoking reviewer agent ${agent::class.simpleName} with reviewer '${reviewerDef.name}'")
                        async { agent.reviewDocuments(reviewerDef, docs) }
                    }
                }.flatten().awaitAll()
            }.forEach { feedback ->
                logI(TAG, "Reviewer '${feedback.reviewerName}' feedback length: ${feedback.content.length} chars")
                println("=== Reviewer: ${feedback.reviewerName} ===\n${feedback.content}")
            }
        }

        val report = ValidationReport(
            runId = java.util.UUID.randomUUID().toString(),
            timestampEpochMs = System.currentTimeMillis(),
            issues = allIssues,
        )
        logI(TAG, "Validation report created: runId=${report.runId}, totalIssues=${report.issues.size}")

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

        val reportPath = docsDir.resolve("validation-report.md")
        Files.writeString(reportPath, reportContent)
        logI(TAG, "Validation report written to: $reportPath")

        return report
    }
}
