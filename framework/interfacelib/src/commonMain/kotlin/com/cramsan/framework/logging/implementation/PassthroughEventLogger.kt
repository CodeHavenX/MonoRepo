package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity

/**
 * A simple implementation of [EventLoggerInterface] that simply delegates calls to the [platformDelegate].
 */
class PassthroughEventLogger(
    private val platformDelegate: EventLoggerDelegate,
) : EventLoggerInterface {

    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        ignoreErrorCallback: Boolean,
        vararg args: Any?,
    ) = platformDelegate.log(
        severity = severity,
        tag = tag,
        message = message,
        throwable = throwable,
        *args,
    )
}
