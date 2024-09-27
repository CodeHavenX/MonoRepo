package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * Noop implementation of [EventLoggerDelegate] that does nothing.
 */
object EventLoggerDelegateNoop : EventLoggerDelegate {

    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?, vararg args: Any?) {
        println("[$severity][$tag] $message, args: $args")
        throwable?.printStackTrace()
    }

    override fun setTargetSeverity(targetSeverity: Severity) = Unit
}
