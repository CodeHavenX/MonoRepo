package com.cramsan.devtools.cli.commands

import com.cramsan.devtools.cli.detectRepoRoot
import com.cramsan.devtools.core.generateManager
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

internal class CreateManagerCommand : CliktCommand(name = "manager") {
    private val name: String by option("--name", help = "PascalCase component name, e.g. Property").required()
    private val app: String by option("--app", help = "Lowercase app name, e.g. edifikana").required()
    private val repoRoot: Path by option("--repo-root", help = "Path to monorepo root (auto-detected if omitted)")
        .path(mustExist = true, canBeFile = false)
        .defaultLazy { detectRepoRoot() }

    override fun run() {
        val result = runGenerator { generateManager(repoRoot, name, app) }
        printResult(result)
    }
}
