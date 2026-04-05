package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.cramsan.agentic.app.agenticModule
import com.cramsan.agentic.core.IssueSeverity
import com.cramsan.agentic.core.IssueStatus
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Path
import kotlin.system.exitProcess

private const val TAG = "ValidateCommand"

class ValidateCommand : CliktCommand(name = "validate", help = "Run a validation pass and exit") {

    private val configPath by option("--config", help = "Path to config.json").default(".agentic/config.json")

    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))

        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val docsDir = agenticDir.resolve("docs")
        val repoRoot = Path.of(".")
        logI(TAG, "Validate started. Config path: $configPath, docs dir: $docsDir")

        val koin = startKoin {
            modules(agenticModule(agenticDir, repoRoot))
        }.koin

        try {
            val validationService = koin.get<com.cramsan.agentic.input.ValidationService>()
            val report = runBlocking { validationService.runValidationPass() }

            val blockingIssues = report.issues.filter {
                it.severity == IssueSeverity.BLOCKING && it.status == IssueStatus.OPEN
            }

            logI(TAG, "Validate completed. Total issues: ${report.issues.size}, blocking issues: ${blockingIssues.size}")

            echo("\n=== Validation Report ===")
            echo("Total issues: ${report.issues.size}")
            echo("Blocking issues: ${blockingIssues.size}")

            if (blockingIssues.isNotEmpty()) {
                logW(TAG, "Validation FAILED: ${blockingIssues.size} blocking issue(s) found that must be resolved")
                echo("\nBlocking issues:")
                blockingIssues.forEach { issue ->
                    echo("  [${issue.id}] ${issue.documentId}: ${issue.description}")
                }
                echo("\nValidation FAILED: ${blockingIssues.size} blocking issue(s) must be resolved.")
                exitProcess(1)
            } else {
                echo("\nValidation PASSED. All documents are ready.")
            }
        } finally {
            stopKoin()
        }
    }
}
