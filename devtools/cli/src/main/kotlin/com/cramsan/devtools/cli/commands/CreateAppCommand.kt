package com.cramsan.devtools.cli.commands

import com.cramsan.devtools.cli.detectRepoRoot
import com.cramsan.devtools.core.generateApp
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path

internal class CreateAppCommand : CliktCommand(name = "app") {
    private val name: String by option("--name", help = "Lowercase app name, e.g. myapp").required()
    private val display: String by option(
        "--display",
        help = "PascalCase display name used in code, e.g. MyApp",
    ).required()
    private val noWasm: Boolean by option("--no-wasm", help = "Exclude the front-end/app-wasm module").flag()
    private val noAndroid: Boolean by option("--no-android", help = "Exclude the front-end/app-android module").flag()
    private val noJvm: Boolean by option("--no-jvm", help = "Exclude the front-end/app-jvm module").flag()
    private val repoRoot: Path by option("--repo-root", help = "Path to monorepo root (auto-detected if omitted)")
        .path(mustExist = true, canBeFile = false)
        .defaultLazy { detectRepoRoot() }

    override fun run() {
        val result =
            generateApp(
                repoRoot = repoRoot,
                appName = name,
                displayName = display,
                includeWasm = !noWasm,
                includeAndroid = !noAndroid,
                includeJvm = !noJvm,
            )
        printResult(result)
    }
}
