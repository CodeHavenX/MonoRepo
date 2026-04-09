package com.cramsan.agentic.input

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.REVISION_SYSTEM_PROMPT
import com.cramsan.agentic.core.StageApprovalRecord
import com.cramsan.agentic.core.StageApprovalWarning
import com.cramsan.agentic.core.StageDocument
import com.cramsan.agentic.core.WorkflowConfig
import com.cramsan.agentic.core.WorkflowConfigError
import com.cramsan.agentic.core.WorkflowConfigErrorType
import com.cramsan.agentic.core.WorkflowPromptConfig
import com.cramsan.agentic.core.WorkflowStageConfig
import com.cramsan.agentic.core.WorkflowState
import com.cramsan.agentic.core.WorkflowStatus
import com.cramsan.agentic.core.resolvePath
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

private const val TAG = "DefaultWorkflowService"

class DefaultWorkflowService(
    private val documentStore: DocumentStore,
    private val aiProvider: AiProvider,
    private val docsDir: Path,
    private val json: Json,
    private val workflowConfig: WorkflowConfig,
) : WorkflowService {

    private val stages: List<WorkflowStageConfig> = workflowConfig.stages
    private val stageById: Map<String, WorkflowStageConfig> = stages.associateBy { it.id }

    override fun getState(): WorkflowState {
        val docs = documentStore.getAll()
        if (docs.isEmpty()) {
            logD(TAG, "getState: no documents found -> NotStarted")
            return WorkflowState(
                status = WorkflowStatus.NotStarted,
                completedStages = emptyList(),
                currentStage = null,
                nextStage = stages.firstOrNull(),
            )
        }
        val completedStages = mutableListOf<String>()
        val approvalWarnings = mutableListOf<StageApprovalWarning>()
        for (stage in stages) {
            val approvalFile = docsDir.resolve(stage.approvalRecordFile)
            if (Files.exists(approvalFile)) {
                completedStages.add(stage.id)
                checkApprovalDrift(stage, approvalFile)?.let { approvalWarnings.add(it) }
            } else {
                val outputFile = docsDir.resolve(stage.outputFile)
                val nextStage = stages.getOrNull(stages.indexOf(stage) + 1)
                return if (Files.exists(outputFile)) {
                    logD(TAG, "getState: stage ${stage.id} pending approval")
                    WorkflowState(
                        status = WorkflowStatus.StagePendingApproval(stage.id),
                        completedStages = completedStages.toList(),
                        currentStage = stage,
                        nextStage = nextStage,
                        approvalWarnings = approvalWarnings.toList(),
                    )
                } else {
                    logD(TAG, "getState: stage ${stage.id} in progress")
                    WorkflowState(
                        status = WorkflowStatus.StageInProgress(stage.id),
                        completedStages = completedStages.toList(),
                        currentStage = stage,
                        nextStage = null,
                        approvalWarnings = approvalWarnings.toList(),
                    )
                }
            }
        }

        logD(TAG, "getState: all stages complete")
        return WorkflowState(
            status = WorkflowStatus.Complete,
            completedStages = completedStages.toList(),
            currentStage = null,
            nextStage = null,
            approvalWarnings = approvalWarnings.toList(),
        )
    }

    override fun getStageConfig(stageId: String): WorkflowStageConfig? = stageById[stageId]

    override fun getAllStages(): List<WorkflowStageConfig> = stages

    override suspend fun startStage(stageId: String): StageDocument {
        val stage = stageById[stageId]
            ?: throw IllegalArgumentException("Unknown stage: $stageId")

        logI(TAG, "Starting stage: ${stage.name} ($stageId)")

        // Verify dependencies are met
        for (depId in stage.inputDependencies) {
            val depStage = stageById[depId]
                ?: throw IllegalStateException("Stage '$stageId' depends on unknown stage '$depId'")
            val approvalFile = docsDir.resolve(depStage.approvalRecordFile)
            require(Files.exists(approvalFile)) {
                "Dependency '${depStage.name}' ($depId) must be approved before starting '$stageId'"
            }
        }

        // Build input content
        val inputContent = buildInputContent(stage)

        // Get prompt from config
        val systemPrompt = resolvePrompt(stage.prompt)

        // Call AI
        val content = callAi(systemPrompt, inputContent)

        // Write output
        val outputPath = docsDir.resolve(stage.outputFile)
        Files.writeString(outputPath, content)
        logI(TAG, "Stage output written to $outputPath")

        return StageDocument(stageId, stage.name, outputPath)
    }

    override suspend fun startNextStage(): StageDocument? {
        val state = getState()
        return when (val status = state.status) {
            is WorkflowStatus.StageInProgress -> {
                startStage(status.stageId)
            }
            is WorkflowStatus.StagePendingApproval -> {
                logI(TAG, "startNextStage: stage ${status.stageId} is pending approval, cannot start next")
                null
            }
            WorkflowStatus.Complete -> {
                logI(TAG, "startNextStage: workflow complete, nothing to start")
                null
            }
            WorkflowStatus.NotStarted -> {
                stages.firstOrNull()?.let { startStage(it.id) }
            }
        }
    }

    override suspend fun reviseStage(stageId: String): StageDocument {
        val stage = stageById[stageId]
            ?: throw IllegalArgumentException("Unknown stage: $stageId")

        logI(TAG, "Revising stage: ${stage.name} ($stageId)")

        val outputFile = docsDir.resolve(stage.outputFile)
        require(Files.exists(outputFile)) {
            "Cannot revise stage '$stageId': output file does not exist"
        }

        val annotatedContent = Files.readString(outputFile)
        val content = callAi(REVISION_SYSTEM_PROMPT.trimIndent(), annotatedContent)
        Files.writeString(outputFile, content)
        logI(TAG, "Revised ${outputFile.fileName} written")

        return StageDocument(stageId, stage.name, outputFile)
    }

    override fun approveStage(stageId: String) {
        val stage = stageById[stageId]
            ?: throw IllegalArgumentException("Unknown stage: $stageId")

        val record = StageApprovalRecord(
            stageId = stageId,
            approvedAtEpochMs = System.currentTimeMillis(),
            inputHashes = computeInputHashes(stage),
        )
        val recordFile = docsDir.resolve(stage.approvalRecordFile)
        Files.createDirectories(recordFile.parent)
        Files.writeString(recordFile, json.encodeToString(record))
        logI(TAG, "Approval record written: $recordFile")
    }

    private fun checkApprovalDrift(stage: WorkflowStageConfig, approvalFile: Path): StageApprovalWarning? {
        return try {
            val record = json.decodeFromString<StageApprovalRecord>(Files.readString(approvalFile))
            val currentHashes = computeInputHashes(stage)
            val changedInputs = record.inputHashes.entries
                .filter { (file, hash) -> currentHashes[file] != hash }
                .map { it.key } +
                currentHashes.keys.filter { it !in record.inputHashes }
            if (changedInputs.isEmpty()) null
            else StageApprovalWarning(stage.id, stage.name, changedInputs)
        } catch (e: Exception) {
            logW(TAG, "Failed to check approval drift for stage ${stage.id}", e)
            null
        }
    }

    private fun computeInputHashes(stage: WorkflowStageConfig): Map<String, String> {
        val hashes = mutableMapOf<String, String>()
        documentStore.getAll().forEach { doc ->
            val filePath = resolvePath(docsDir, doc.relativePath)
            if (Files.exists(filePath)) {
                hashes[doc.relativePath] = computeFileHash(filePath)
            }
        }
        for (depId in stage.inputDependencies) {
            val depStage = stageById[depId] ?: continue
            val depFile = docsDir.resolve(depStage.outputFile)
            if (Files.exists(depFile)) {
                hashes[depStage.outputFile] = computeFileHash(depFile)
            }
        }
        return hashes
    }

    private fun computeFileHash(path: Path): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(Files.readAllBytes(path))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    override fun validateWorkflowConfig(): List<WorkflowConfigError> {
        val errors = mutableListOf<WorkflowConfigError>()
        val stageIds = stages.map { it.id }.toSet()

        // Check for duplicate IDs
        val duplicates = stages.groupBy { it.id }.filter { it.value.size > 1 }
        for ((id, _) in duplicates) {
            errors.add(
                WorkflowConfigError(
                    type = WorkflowConfigErrorType.DUPLICATE_STAGE_ID,
                    message = "Duplicate stage ID: $id",
                    stageId = id,
                )
            )
        }

        // Check for missing dependencies
        for (stage in stages) {
            for (depId in stage.inputDependencies) {
                if (depId !in stageIds) {
                    errors.add(
                        WorkflowConfigError(
                            type = WorkflowConfigErrorType.MISSING_DEPENDENCY,
                            message = "Stage '${stage.id}' depends on unknown stage '$depId'",
                            stageId = stage.id,
                        )
                    )
                }
            }
        }

        // Check for circular dependencies
        errors.addAll(detectCircularDependencies())

        return errors
    }

    private fun detectCircularDependencies(): List<WorkflowConfigError> {
        val errors = mutableListOf<WorkflowConfigError>()
        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()

        fun dfs(stageId: String, path: List<String>): Boolean {
            if (stageId in recursionStack) {
                val cycleStart = path.indexOf(stageId)
                val cycle = path.subList(cycleStart, path.size) + stageId
                errors.add(
                    WorkflowConfigError(
                        type = WorkflowConfigErrorType.CIRCULAR_DEPENDENCY,
                        message = "Circular dependency detected: ${cycle.joinToString(" -> ")}",
                        stageId = stageId,
                    )
                )
                return true
            }
            if (stageId in visited) return false

            visited.add(stageId)
            recursionStack.add(stageId)

            val stage = stageById[stageId]
            if (stage != null) {
                for (depId in stage.inputDependencies) {
                    if (dfs(depId, path + stageId)) {
                        return true
                    }
                }
            }

            recursionStack.remove(stageId)
            return false
        }

        for (stage in stages) {
            if (stage.id !in visited) {
                dfs(stage.id, emptyList())
            }
        }

        return errors
    }

    private fun buildInputContent(stage: WorkflowStageConfig): String {
        return buildString {
            // Add input documents
            append(readInputDocs())

            // Add outputs from dependency stages
            for (depId in stage.inputDependencies) {
                val depStage = stageById[depId] ?: continue
                val depFile = docsDir.resolve(depStage.outputFile)
                if (Files.exists(depFile)) {
                    val depContent = Files.readString(depFile)
                    appendLine()
                    appendLine("---")
                    appendLine("## Approved ${depStage.name}")
                    appendLine()
                    appendLine(depContent)
                }
            }
        }
    }

    private fun resolvePrompt(promptConfig: WorkflowPromptConfig): String {
        return when (promptConfig) {
            is WorkflowPromptConfig.Inline -> promptConfig.systemPrompt
            is WorkflowPromptConfig.File -> {
                val promptPath = docsDir.resolve(promptConfig.path)
                Files.readString(promptPath)
            }
        }
    }

    private fun readInputDocs(): String {
        val docs = documentStore.getAll()
        return buildString {
            docs.forEach { doc ->
                val filePath = resolvePath(docsDir, doc.relativePath)
                if (Files.exists(filePath)) {
                    appendLine("---")
                    appendLine("## ${doc.typeId.replace('-', ' ').replaceFirstChar { it.uppercase() }}")
                    appendLine()
                    appendLine(Files.readString(filePath))
                    appendLine()
                }
            }
        }
    }

    private suspend fun callAi(systemPrompt: String, userContent: String): String {
        val response = aiProvider.chat(
            systemPrompt = systemPrompt,
            messages = listOf(AiMessage("user", userContent)),
            tools = emptyList(),
        )
        val text = response.content.filterIsInstance<AiContentBlock.Text>().firstOrNull()?.text
            ?: error("AI returned no text content")
        return text.trim()
    }
}
