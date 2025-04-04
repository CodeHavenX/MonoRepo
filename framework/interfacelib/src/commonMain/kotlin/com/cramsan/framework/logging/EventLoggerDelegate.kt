package com.cramsan.framework.logging

/**
 * Delegate to be implemented for each platform to provide logging.
 */
interface EventLoggerDelegate {

    /**
     * Log a [message] and [tag]. If the [severity] is less than [severity], the message is not logged.
     * There is also an optional [throwable] that can be logged. The [args] can be provided to apply as
     * arguments to format into the [message].
     */
    fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        vararg args: Any?,
    )
}
