package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallbackInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity

class EventLogger(
    override val targetSeverity: Severity,
    override val errorCallback: EventLoggerErrorCallbackInterface?,
    override val platformDelegate: EventLoggerDelegate
) : EventLoggerInterface {

    override fun log(severity: Severity, tag: String, message: String) {
        if (severity < targetSeverity)
            return
        platformDelegate.log(severity, tag, message)
        errorCallback?.let {
            if (severity == Severity.WARNING) {
                it.onWarning(tag, message)
            } else if (severity == Severity.ERROR) {
                it.onError(tag, message)
            }
        }
    }
}