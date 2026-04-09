package com.cramsan.agentic.app.commands

import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.core.WorkflowStatus
import com.cramsan.agentic.input.WorkflowService
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path
import kotlin.system.exitProcess

class PlanCommand : CliktCommand(name = "plan", help = "Planning phase commands", invokeWithoutSubcommand = false) {
    override fun run() = Unit

    init {
        subcommands(
            PlanStatusSubcommand(),
            PlanStartSubcommand(),
            PlanApproveSubcommand(),
            PlanReviseSubcommand(),
            PlanStagesSubcommand(),
        )
    }
}

private class PlanStatusSubcommand : CliktCommand(
    name = "status",
    help = "Print current planning phase and next required action",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val workflowService = koin.get<WorkflowService>()
            val state = workflowService.getState()

            echo("Workflow status: ${formatStatus(state.status)}")
            if (state.completedStages.isNotEmpty()) {
                echo("Completed stages: ${state.completedStages.joinToString(", ")}")
            }
            state.currentStage?.let { echo("Current stage: ${it.name} (${it.id})") }
            echo(nextActionMessage(state.status, state.currentStage?.id))
        } finally {
            stopKoin()
        }
    }

    private fun formatStatus(status: WorkflowStatus): String = when (status) {
        WorkflowStatus.NotStarted -> "NOT_STARTED"
        is WorkflowStatus.StageInProgress -> "STAGE_IN_PROGRESS (${status.stageId})"
        is WorkflowStatus.StagePendingApproval -> "STAGE_PENDING_APPROVAL (${status.stageId})"
        WorkflowStatus.Complete -> "COMPLETE"
    }

    private fun nextActionMessage(status: WorkflowStatus, stageId: String?): String = when (status) {
        WorkflowStatus.NotStarted -> "Next: provide input documents and run 'agentic plan start'."
        is WorkflowStatus.StageInProgress -> "Next: run 'agentic plan start' to produce the ${stageId ?: "next"} output."
        is WorkflowStatus.StagePendingApproval -> "Next: review output, then run 'agentic plan approve $stageId' or 'agentic plan revise $stageId'."
        WorkflowStatus.Complete -> "Planning is complete. Run 'agentic start' to begin execution."
    }
}

private class PlanStartSubcommand : CliktCommand(
    name = "start",
    help = "Start a planning stage (auto-advances if no stage ID provided)",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val stageId by argument(help = "Stage ID to start (optional, auto-advances if omitted)").optional()

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val workflowService = koin.get<WorkflowService>()

            // Validate workflow config first
            val errors = workflowService.validateWorkflowConfig()
            if (errors.isNotEmpty()) {
                echo("Workflow configuration errors:")
                errors.forEach { echo("  - ${it.message}") }
                exitProcess(1)
            }

            val state = workflowService.getState()

            when (state.status) {
                WorkflowStatus.NotStarted -> {
                    echo("Cannot start planning: no input documents found.")
                    exitProcess(1)
                }
                is WorkflowStatus.StagePendingApproval -> {
                    val pendingStageId = state.status.stageId
                    echo("Stage '$pendingStageId' is awaiting approval.")
                    echo("Run 'agentic plan approve $pendingStageId' or 'agentic plan revise $pendingStageId'.")
                    exitProcess(1)
                }
                WorkflowStatus.Complete -> {
                    echo("Planning is already complete. Run 'agentic start' to begin execution.")
                    exitProcess(1)
                }
                is WorkflowStatus.StageInProgress -> {
                    val targetStageId = stageId ?: state.status.stageId
                    val stage = workflowService.getStageConfig(targetStageId)
                        ?: run {
                            echo("Unknown stage: $targetStageId")
                            echo("Run 'agentic plan stages' to see available stages.")
                            exitProcess(1)
                        }

                    echo("Starting stage: ${stage.name} ($targetStageId)...")
                    val doc = runBlocking { workflowService.startStage(targetStageId) }
                    echo("Output written to ${doc.path}")
                    echo("Review the output and run 'agentic plan approve $targetStageId' or 'agentic plan revise $targetStageId'.")
                }
            }
        } finally {
            stopKoin()
        }
    }
}

private class PlanApproveSubcommand : CliktCommand(
    name = "approve",
    help = "Approve a planning stage output",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val stageId by argument(help = "Stage ID to approve")

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val workflowService = koin.get<WorkflowService>()
            val stage = workflowService.getStageConfig(stageId)
                ?: run {
                    echo("Unknown stage: $stageId")
                    echo("Run 'agentic plan stages' to see available stages.")
                    exitProcess(1)
                }

            workflowService.approveStage(stageId)
            echo("Stage '${stage.name}' ($stageId) approved.")

            val newState = workflowService.getState()
            if (newState.status == WorkflowStatus.Complete) {
                echo("Planning complete. Run 'agentic start' to begin execution.")
            } else {
                echo("Run 'agentic plan start' to proceed to the next stage.")
            }
        } finally {
            stopKoin()
        }
    }
}

private class PlanReviseSubcommand : CliktCommand(
    name = "revise",
    help = "Re-run a planning stage after annotating the document with feedback",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val stageId by argument(help = "Stage ID to revise")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val workflowService = koin.get<WorkflowService>()
            val stage = workflowService.getStageConfig(stageId)
                ?: run {
                    echo("Unknown stage: $stageId")
                    echo("Run 'agentic plan stages' to see available stages.")
                    exitProcess(1)
                }

            echo("Revising stage: ${stage.name} ($stageId)...")
            val doc = runBlocking { workflowService.reviseStage(stageId) }
            echo("Revised document written to ${doc.path}")
            echo("Review and run 'agentic plan approve $stageId' or re-annotate and revise again.")
        } finally {
            stopKoin()
        }
    }
}

private class PlanStagesSubcommand : CliktCommand(
    name = "stages",
    help = "List all configured workflow stages",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val workflowService = koin.get<WorkflowService>()
            val state = workflowService.getState()
            val stages = workflowService.getAllStages()

            echo("Configured Workflow Stages:")
            echo("-".repeat(60))

            for (stage in stages) {
                val status = when {
                    stage.id in state.completedStages -> "[APPROVED]"
                    state.currentStage?.id == stage.id -> {
                        if (state.status is WorkflowStatus.StagePendingApproval) "[PENDING APPROVAL]"
                        else "[IN PROGRESS]"
                    }
                    else -> "[NOT STARTED]"
                }
                echo("  ${stage.id}: ${stage.name} $status")
                echo("    Output: ${stage.outputFile}")
                if (stage.inputDependencies.isNotEmpty()) {
                    echo("    Depends on: ${stage.inputDependencies.joinToString(", ")}")
                }
            }
        } finally {
            stopKoin()
        }
    }
}
