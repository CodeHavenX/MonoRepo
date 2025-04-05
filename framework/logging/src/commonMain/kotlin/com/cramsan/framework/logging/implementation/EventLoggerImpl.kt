package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.EventLoggerErrorCallback
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity

/**
 * Implement the [log] function that is used across all platforms. This class performs the severity
 * check and then it delegates the logging implementation to [platformDelegate]. An [errorCallback]
 * can be provided.
 */
class EventLoggerImpl(
    private val targetSeverity: Severity,
    errorCallback: EventLoggerErrorCallback?,
    private val platformDelegate: EventLoggerDelegate,
) : EventLoggerInterface {

    private var _errorCallback = errorCallback
    val errorCallback: EventLoggerErrorCallback?
        get() = _errorCallback

    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        ignoreErrorCallback: Boolean,
        vararg args: Any?,
    ) {
        if (severity < targetSeverity) {
            return
        }

        platformDelegate.log(severity, tag, message, throwable, *args)

        if (ignoreErrorCallback) {
            return
        }
        errorCallback?.let {
            if (severity == Severity.WARNING) {
                it.onWarning(tag, message, throwable)
            } else if (severity == Severity.ERROR) {
                it.onError(tag, message, throwable)
            }
        }
    }
}
