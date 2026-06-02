package com.cramsan.devtools.cli

import com.cramsan.devtools.cli.commands.CreateCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

internal class DevToolsCli : NoOpCliktCommand(name = "devtools") {
    init {
        subcommands(CreateCommand())
    }
}
