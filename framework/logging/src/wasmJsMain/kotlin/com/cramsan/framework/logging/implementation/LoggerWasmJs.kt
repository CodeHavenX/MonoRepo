package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * A simple logger implementation that logs to the console in yhe Wasm platform.
 */
class LoggerWasmJs : EventLoggerDelegate {
    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        vararg args: Any?,
    ) {
        val argumentList = args.toList()

        val formattedString = if (argumentList.isEmpty()) {
            "[${severity.name}][$tag]$message"
        } else {
            "[${severity.name}][$tag]$message-$argumentList"
        }

        when (severity) {
            Severity.VERBOSE, Severity.DEBUG -> println(formattedString)
            Severity.INFO -> println(formattedString)
            Severity.WARNING -> println(formattedString)
            Severity.ERROR -> println(formattedString)
            Severity.DISABLED -> Unit
        }
        throwable?.let {
            println(it.message)
            it.printStackTrace()
        }
    }
}
