package com.cramsan.devtools.cli.commands

import com.cramsan.devtools.core.GenerationResult
import com.github.ajalt.clikt.core.CliktCommand

internal fun CliktCommand.printResult(result: GenerationResult) {
    echo("Created:")
    result.createdFiles.forEach { echo("  $it") }
    if (result.postGenerationChecklist.isNotEmpty()) {
        echo("")
        result.postGenerationChecklist.forEach { echo(it) }
    }
}
