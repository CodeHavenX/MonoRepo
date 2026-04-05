package com.cramsan.agentic.app

import com.cramsan.agentic.app.commands.InitCommand
import com.cramsan.agentic.app.commands.ResumeCommand
import com.cramsan.agentic.app.commands.StartCommand
import com.cramsan.agentic.app.commands.StatusCommand
import com.cramsan.agentic.app.commands.TaskCommand
import com.cramsan.agentic.app.commands.ValidateCommand
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
            ValidateCommand(),
            StartCommand(),
            ResumeCommand(),
            StatusCommand(),
            TaskCommand(),
        )
    }
}
