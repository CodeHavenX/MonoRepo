package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class RunCommand : CliktCommand(name = "run", help = "Orchestrator execution commands", invokeWithoutSubcommand = false) {
    override fun run() = Unit

    init {
        subcommands(
            StartCommand(),
            StatusCommand(),
            TaskCommand(),
        )
    }
}
