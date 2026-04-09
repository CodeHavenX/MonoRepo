package com.cramsan.agentic.app

import com.cramsan.agentic.app.commands.InitCommand
import com.cramsan.agentic.app.commands.PlanCommand
import com.cramsan.agentic.app.commands.RunCommand
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class AgenticCli : CliktCommand(name = "agentic", invokeWithoutSubcommand = false) {
    override fun run() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    init {
        subcommands(
            InitCommand(),
            PlanCommand(),
            RunCommand(),
        )
    }
}
