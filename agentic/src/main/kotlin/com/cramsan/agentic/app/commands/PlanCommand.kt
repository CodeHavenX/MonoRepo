package com.cramsan.agentic.app.commands

import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.IssueStatus
import com.cramsan.agentic.core.PlanningStage
import com.cramsan.agentic.core.PlanningStatus
import com.cramsan.agentic.input.PlanningService
import com.cramsan.agentic.input.ValidationService
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.logging.logW
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
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
            PlanValidateSubcommand(),
            PlanStatusSubcommand(),
            PlanGenerateSubcommand(),
            PlanApproveSubcommand(),
            PlanReviseSubcommand(),
        )
    }
}

private class PlanValidateSubcommand : CliktCommand(
    name = "validate",
    help = "Run a document validation pass and exit (non-zero if blocking issues remain)",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val validationService = koin.get<ValidationService>()
            val report = runBlocking { validationService.runValidationPass() }

            val blockingIssues = report.issues.filter {
                it.severity == IssueSeverity.BLOCKING && it.status == IssueStatus.OPEN
            }

            echo("\n=== Validation Report ===")
            echo("Total issues: ${report.issues.size}")
            echo("Blocking issues: ${blockingIssues.size}")

            if (blockingIssues.isNotEmpty()) {
                logW("PlanValidateSubcommand", "Validation FAILED: ${blockingIssues.size} blocking issue(s) found")
                echo("\nBlocking issues:")
                blockingIssues.forEach { issue ->
                    echo("  [${issue.id}] ${issue.documentId}: ${issue.description}")
                }
                echo("\nValidation FAILED: ${blockingIssues.size} blocking issue(s) must be resolved.")
                exitProcess(1)
            } else {
                echo("\nValidation PASSED. All documents are ready.")
                echo("Next step: run 'agentic plan generate' to begin planning.")
            }
        } finally {
            stopKoin()
        }
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
            val planningService = koin.get<PlanningService>()
            val currentStatus = planningService.status()

            echo("Planning status: ${currentStatus.name}")
            echo(nextActionMessage(currentStatus))
        } finally {
            stopKoin()
        }
    }

    private fun nextActionMessage(status: PlanningStatus): String = when (status) {
        PlanningStatus.NOT_STARTED -> "Next: provide input documents and run 'agentic plan validate'."
        PlanningStatus.AWAITING_DOCUMENT_VALIDATION -> "Next: run 'agentic plan validate' to validate input documents."
        PlanningStatus.STAGE_1_IN_PROGRESS -> "Next: run 'agentic plan generate' to produce the high-level plan."
        PlanningStatus.STAGE_1_PENDING_APPROVAL -> "Next: review high-level-plan.md, then run 'agentic plan approve stage1' or 'agentic plan revise stage1'."
        PlanningStatus.STAGE_2_IN_PROGRESS -> "Next: run 'agentic plan generate' to produce the low-level plan."
        PlanningStatus.STAGE_2_PENDING_APPROVAL -> "Next: review low-level-plan.md, then run 'agentic plan approve stage2' or 'agentic plan revise stage2'."
        PlanningStatus.STAGE_3_IN_PROGRESS -> "Next: run 'agentic plan generate' to produce the task list."
        PlanningStatus.STAGE_3_PENDING_APPROVAL -> "Next: review task-list.md, then run 'agentic plan approve stage3' or 'agentic plan revise stage3'."
        PlanningStatus.COMPLETE -> "Planning is complete. Run 'agentic start' to begin execution."
    }
}

private class PlanGenerateSubcommand : CliktCommand(
    name = "generate",
    help = "Advance to and run the next planning stage",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val planningService = koin.get<PlanningService>()

            when (planningService.status()) {
                PlanningStatus.STAGE_1_IN_PROGRESS -> {
                    echo("Generating high-level plan...")
                    val doc = runBlocking { planningService.generateHighLevelPlan() }
                    echo("High-level plan written to ${doc.path}")
                    echo("Review the plan and run 'agentic plan approve stage1' or 'agentic plan revise stage1'.")
                }
                PlanningStatus.STAGE_2_IN_PROGRESS -> {
                    echo("Generating low-level plan...")
                    val doc = runBlocking { planningService.generateLowLevelPlan() }
                    echo("Low-level plan written to ${doc.path}")
                    echo("Review the plan and run 'agentic plan approve stage2' or 'agentic plan revise stage2'.")
                }
                PlanningStatus.STAGE_3_IN_PROGRESS -> {
                    echo("Generating task list...")
                    val doc = runBlocking { planningService.generateTaskList() }
                    echo("Task list written to ${doc.path}")
                    echo("Review the task list and run 'agentic plan approve stage3' or 'agentic plan revise stage3'.")
                }
                PlanningStatus.NOT_STARTED,
                PlanningStatus.AWAITING_DOCUMENT_VALIDATION -> {
                    echo("Cannot generate plan: documents are not yet validated.")
                    echo("Run 'agentic plan validate' first.")
                    exitProcess(1)
                }
                PlanningStatus.STAGE_1_PENDING_APPROVAL,
                PlanningStatus.STAGE_2_PENDING_APPROVAL,
                PlanningStatus.STAGE_3_PENDING_APPROVAL -> {
                    echo("Current stage output is awaiting approval.")
                    echo("Run 'agentic plan approve <stage>' or 'agentic plan revise <stage>'.")
                    exitProcess(1)
                }
                PlanningStatus.COMPLETE -> {
                    echo("Planning is already complete. Run 'agentic start' to begin execution.")
                    exitProcess(1)
                }
            }
        } finally {
            stopKoin()
        }
    }
}

private class PlanApproveSubcommand : CliktCommand(
    name = "approve",
    help = "Approve a planning stage output (stage1 | stage2 | stage3)",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val stage by argument().convert { parseStage(it) }

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val planningService = koin.get<PlanningService>()
            planningService.approve(stage)
            echo("Stage ${stage.label} approved.")
            if (stage == PlanningStage.TASK_LIST) {
                echo("Planning complete. Run 'agentic start' to begin execution.")
            } else {
                echo("Run 'agentic plan generate' to proceed to the next stage.")
            }
        } finally {
            stopKoin()
        }
    }
}

private class PlanReviseSubcommand : CliktCommand(
    name = "revise",
    help = "Re-run a planning stage after annotating the document with feedback (stage1 | stage2 | stage3)",
) {
    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")
    private val stage by argument().convert { parseStage(it) }

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(agenticModule(agenticDir, Path.of("."))) }.koin

        try {
            val planningService = koin.get<PlanningService>()
            echo("Revising ${stage.label}...")
            val doc = runBlocking { planningService.revise(stage) }
            echo("Revised document written to ${doc.path}")
            echo("Review and run 'agentic plan approve ${stage.label}' or re-annotate and revise again.")
        } finally {
            stopKoin()
        }
    }
}

private fun parseStage(value: String): PlanningStage =
    PlanningStage.entries.firstOrNull { it.label == value }
        ?: throw IllegalArgumentException("Unknown stage '$value'. Use: stage1, stage2, or stage3.")
