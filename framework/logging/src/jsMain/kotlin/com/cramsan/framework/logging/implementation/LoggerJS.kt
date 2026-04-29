package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * Logger that uses the Javascript APIs.
 */
class LoggerJS : EventLoggerDelegate {
    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        vararg args: Any?,
    ) {
        val formattedMessage =
            if (args.isNotEmpty()) {
                args.fold(message) { acc, arg -> acc.replaceFirst(FORMAT_SPECIFIER_REGEX, arg?.toString() ?: "null") }
            } else {
                message
            }
        val formattedString = "[${severity.name}][$tag]$formattedMessage"
        when (severity) {
            Severity.VERBOSE, Severity.DEBUG -> console.log(formattedString)
            Severity.INFO -> console.info(formattedString)
            Severity.WARNING -> console.warn(formattedString)
            Severity.ERROR -> console.error(formattedString)
            Severity.DISABLED -> Unit
        }
        throwable?.let {
            console.error(it.message)
            it.printStackTrace()
        }
    }

    companion object {
        private val FORMAT_SPECIFIER_REGEX = Regex("%[sdifbh%]")
    }
}
