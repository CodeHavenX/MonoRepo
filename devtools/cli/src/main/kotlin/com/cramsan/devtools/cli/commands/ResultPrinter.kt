package com.cramsan.devtools.cli.commands

import com.cramsan.devtools.core.GenerationResult
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError

internal fun CliktCommand.printResult(result: GenerationResult) {
    echo("Created:")
    result.createdFiles.forEach { echo("  $it") }
    if (result.postGenerationChecklist.isNotEmpty()) {
        echo("")
        result.postGenerationChecklist.forEach { echo(it) }
    }
}

/**
 * Runs [block] and converts any [IllegalArgumentException] from the generator into a user-friendly
 * [CliktError] so that Clikt prints a clean one-line error instead of a raw stack trace.
 * The original exception is preserved as the cause.
 */
internal fun CliktCommand.runGenerator(block: () -> GenerationResult): GenerationResult =
    try {
        block()
    } catch (e: IllegalArgumentException) {
        throw CliktError(e.message, e, statusCode = 1, printError = true)
    }
