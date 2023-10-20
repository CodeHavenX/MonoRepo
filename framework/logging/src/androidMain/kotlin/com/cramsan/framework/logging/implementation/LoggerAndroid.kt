package com.cramsan.framework.logging.implementation

import android.util.Log
import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * Logger that outputs to the standard Android logger.
 */
class LoggerAndroid : EventLoggerDelegate {

    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        vararg args: Any?,
    ) {
        val formattedMessage = if (args.isNotEmpty()) {
            message.format(args)
        } else {
            message
        }

        when (severity) {
            Severity.VERBOSE -> Log.v(tag, formattedMessage)
            Severity.DEBUG -> Log.d(tag, formattedMessage)
            Severity.INFO -> Log.i(tag, formattedMessage)
            Severity.WARNING -> Log.w(tag, formattedMessage, throwable)
            Severity.ERROR -> Log.e(tag, formattedMessage, throwable)
            Severity.DISABLED -> Unit
        }
    }

    override fun setTargetSeverity(targetSeverity: Severity) = Unit
}
