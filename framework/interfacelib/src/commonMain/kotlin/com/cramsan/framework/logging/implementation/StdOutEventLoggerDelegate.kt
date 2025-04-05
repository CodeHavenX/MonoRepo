package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * A simple implementation of [EventLoggerDelegate] that logs to standard output.
 */
class StdOutEventLoggerDelegate : EventLoggerDelegate {

    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?, vararg args: Any?) {
        if (args.isEmpty()) {
            println("[$severity][$tag] $message")
        } else {
            println("[$severity][$tag] $message, args: ${args.toList()}")
        }
        throwable?.printStackTrace()
    }
}
