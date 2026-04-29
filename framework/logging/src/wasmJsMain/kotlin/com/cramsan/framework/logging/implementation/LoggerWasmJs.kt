package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

@JsFun("(msg) => console.log(msg)")
private external fun jsConsoleLog(msg: String)

@JsFun("(msg) => console.info(msg)")
private external fun jsConsoleInfo(msg: String)

@JsFun("(msg) => console.warn(msg)")
private external fun jsConsoleWarn(msg: String)

@JsFun("(msg) => console.error(msg)")
private external fun jsConsoleError(msg: String)

/**
 * A simple logger implementation that logs to the console in the Wasm platform.
 */
class LoggerWasmJs : EventLoggerDelegate {
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
            Severity.VERBOSE, Severity.DEBUG -> jsConsoleLog(formattedString)
            Severity.INFO -> jsConsoleInfo(formattedString)
            Severity.WARNING -> jsConsoleWarn(formattedString)
            Severity.ERROR -> jsConsoleError(formattedString)
            Severity.DISABLED -> Unit
        }
        throwable?.let {
            jsConsoleError(it.message ?: "")
            it.printStackTrace()
        }
    }

    companion object {
        private val FORMAT_SPECIFIER_REGEX = Regex("%[sdifbh%]")
    }
}
