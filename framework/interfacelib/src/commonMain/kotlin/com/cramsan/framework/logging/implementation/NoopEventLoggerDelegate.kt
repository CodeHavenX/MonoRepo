package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

/**
 * Noop implementation of [EventLoggerDelegate] that does nothing.
 */
class NoopEventLoggerDelegate : EventLoggerDelegate {

    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?, vararg args: Any?) = Unit
}
