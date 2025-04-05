package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.Severity

/**
 * A no-op implementation of [EventLoggerErrorCallbackDelegate].
 */
class NoopEventLoggerErrorCallbackDelegate : EventLoggerErrorCallbackDelegate {
    override fun handleErrorEvent(tag: String, message: String, throwable: Throwable, severity: Severity) = Unit
}
