package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator

/**
 * Logger that prints to stdout.
 */
class LoggerJVM(
    private val logger: Logger,
    targetSeverity: Severity,
) : EventLoggerDelegate {

    init {
        // Set the logger level to the target severity
        Configurator.setAllLevels(
            LogManager.getRootLogger().name,
            targetSeverity.toLevel(),
        )
    }

    override fun log(
        severity: Severity,
        tag: String,
        message: String,
        throwable: Throwable?,
        vararg args: Any?,
    ) {
        val level = severity.toLevel()
        val formattedMessage = if (args.isNotEmpty()) {
            message.format(*args)
        } else {
            message
        }
        val logMessage = "[$tag]$formattedMessage"
        logger.log(level, logMessage, throwable)
    }

    companion object {

        const val FILENAME = "app.log"

        /**
         * Return the [Level] that maps to [this] [Severity].
         */
        fun Severity.toLevel(): Level = when (this) {
            Severity.DISABLED -> Level.OFF
            Severity.ERROR -> Level.ERROR
            Severity.WARNING -> Level.WARN
            Severity.INFO -> Level.INFO
            Severity.DEBUG -> Level.DEBUG
            Severity.VERBOSE -> Level.TRACE
        }
    }
}
