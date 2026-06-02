package com.cramsan.devtools.cli.commands

import com.cramsan.devtools.cli.detectRepoRoot
import com.cramsan.devtools.core.generateActivity
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

internal class CreateActivityCommand : CliktCommand(name = "activity") {
    private val name: String by option("--name", help = "PascalCase activity name, e.g. Auth").required()
    private val parent: String by option(
        "--parent",
        help =
        "Repo-relative path to the parent features directory, " +
            "e.g. edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
    ).required()
    private val repoRoot: Path by option("--repo-root", help = "Path to monorepo root (auto-detected if omitted)")
        .path(mustExist = true, canBeFile = false)
        .defaultLazy { detectRepoRoot() }

    override fun run() {
        val result = generateActivity(repoRoot, name, parent)
        printResult(result)
    }
}
