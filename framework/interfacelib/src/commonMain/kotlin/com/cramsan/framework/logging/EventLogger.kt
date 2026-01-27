package com.cramsan.framework.logging

import com.cramsan.framework.logging.EventLogger.singleton
import kotlin.native.concurrent.ThreadLocal

/**
 * Singleton that manages an instance of an [EventLoggerInterface]. The [singleton] starts as
 * null and therefore the caller needs to ensure to provide an instance. If an
 * instance is not set, then any calls to a method that tries to access it will result in a
 * [Throwable] being thrown.
 *
 * @Author cramsan
 * @created 1/17/2021
 */
@ThreadLocal
object EventLogger {

    private lateinit var _singleton: EventLoggerInterface

    /**
     * Global [EventLoggerInterface] singleton
     */
    val singleton: EventLoggerInterface
        get() {
            return if (::_singleton.isInitialized) {
                _singleton
            } else {
                error("EventLogger not initialized. Please call EventLogger.setInstance() before using it.")
            }
        }

    /**
     * Set the instance to be used for the [singleton].
     */
    fun setInstance(eventLogger: EventLoggerInterface) {
        _singleton = eventLogger
    }
}

// List of global functions to provide an easy API for logging

/**
 * Global function that delegates to [EventLogger.singleton] and calls [EventLoggerInterface.log]
 * severity [Severity.VERBOSE].
 *
 * @see EventLoggerInterface.log
 */
fun logV(tag: String, message: String, vararg args: Any?) {
    singleton.v(tag, message, *args)
}

/**
 * Global function that delegates to [EventLogger.singleton] and calls [EventLoggerInterface.log]
 * severity [Severity.DEBUG].
 *
 * @see EventLoggerInterface.log
 */
fun logD(tag: String, message: String, vararg args: Any?) {
    singleton.d(tag, message, *args)
}

/**
 * Global function that delegates to [EventLogger.singleton] and calls [EventLoggerInterface.log]
 * severity [Severity.INFO].
 *
 * @see EventLoggerInterface.log
 */
fun logI(tag: String, message: String) {
    singleton.i(tag, message)
}

/**
 * Global function that delegates to [EventLogger.singleton] and calls [EventLoggerInterface.log]
 * severity [Severity.WARNING].
 *
 * @see EventLoggerInterface.log
 */
fun logW(tag: String, message: String, throwable: Throwable? = null) {
    singleton.w(tag, message, throwable)
}

/**
 * Global function that delegates to [EventLogger.singleton] and calls [EventLoggerInterface.log]
 * severity [Severity.ERROR].
 *
 * @see EventLoggerInterface.log
 */
fun logE(tag: String, message: String, throwable: Throwable? = null) {
    singleton.e(tag, message, throwable)
}
