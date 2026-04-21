package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

/**
 * CLI command group for autonomous agent execution commands. Requires the planning phase
 * (`agentic plan`) to be complete before `run start` is invoked.
 *
 * Subcommands:
 * - `start` — launch the orchestrator polling loop
 * - `status` — print a one-shot status table
 * - `task` — inspect and manage individual tasks
 */
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
