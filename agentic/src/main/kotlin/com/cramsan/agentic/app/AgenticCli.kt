package com.cramsan.agentic.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.cramsan.agentic.app.commands.InitCommand
import com.cramsan.agentic.app.commands.ResumeCommand
import com.cramsan.agentic.app.commands.StartCommand
import com.cramsan.agentic.app.commands.StatusCommand
import com.cramsan.agentic.app.commands.TaskCommand
import com.cramsan.agentic.app.commands.ValidateCommand

class AgenticCli : CliktCommand(name = "agentic", invokeWithoutSubcommand = false) {
    override fun run() = Unit

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
