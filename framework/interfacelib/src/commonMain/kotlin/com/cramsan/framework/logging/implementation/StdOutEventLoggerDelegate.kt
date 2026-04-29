package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * A simple implementation of [EventLoggerDelegate] that logs to standard output.
 */
class StdOutEventLoggerDelegate : EventLoggerDelegate {
    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?, vararg args: Any?) {
        val formattedMessage =
            if (args.isNotEmpty()) {
                args.fold(message) { acc, arg -> acc.replaceFirst(FORMAT_SPECIFIER_REGEX, arg?.toString() ?: "null") }
            } else {
                message
            }
        println("[$severity][$tag] $formattedMessage")
        throwable?.printStackTrace()
    }

    companion object {
        private val FORMAT_SPECIFIER_REGEX = Regex("%[sdifbh%]")
    }
}
