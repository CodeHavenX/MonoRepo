package com.cramsan.devtools.cli.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

internal class CreateCommand : NoOpCliktCommand(name = "create") {
    init {
        subcommands(
            CreateActivityCommand(),
            CreateApiCommand(),
            CreateAppCommand(),
            CreateControllerCommand(),
            CreateDatastoreCommand(),
            CreateFeatureCommand(),
            CreateFrontendServiceCommand(),
            CreateManagerCommand(),
            CreateServiceCommand(),
        )
    }
}
