package com.cramsan.agentic.app

import com.cramsan.agentic.app.commands.InitCommand
import com.cramsan.agentic.app.commands.PlanCommand
import com.cramsan.agentic.app.commands.RunCommand
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum

class AgenticCli : CliktCommand(name = "agentic", invokeWithoutSubcommand = false) {

    private val logLevel by option(
        "--log-level", "-l",
        help = "Logging verbosity: VERBOSE, DEBUG, INFO, WARNING, ERROR, DISABLED (default)",
    ).enum<Severity>(ignoreCase = true).default(Severity.INFO)

    override fun run() {
        EventLogger.setInstance(EventLoggerImpl(logLevel, null, StdOutEventLoggerDelegate()))
    }

    init {
        subcommands(
            InitCommand(),
            PlanCommand(),
            RunCommand(),
        )
    }
}
