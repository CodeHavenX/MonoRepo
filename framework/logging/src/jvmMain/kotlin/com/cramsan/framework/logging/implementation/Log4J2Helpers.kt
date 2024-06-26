package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.LoggerJVM.Companion.toLevel
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.DefaultConfiguration
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

/**
 * Helper class to configure Log4J2.
 *
 * For documentation on how to configure Log4J2, see:
 * https://logging.apache.org/log4j/2.x/manual/configuration.html
 *
 * This logger uses log4j2 with a default configuration to be used as part of the framework library.
 * You can also configure it following the regular log4j2 configurations. If any log4j2 configurations
 * are present, our default configuration will be ignored.
 */
object Log4J2Helpers {

    /**
     * Configures Log4J2 and returns the root logger.
     */
    fun getRootLogger(
        logToFile: Boolean,
        initializationLogLevel: Severity,
    ): Logger {
        val context: LoggerContext = LogManager.getContext(false) as LoggerContext
        val config: Configuration = context.configuration

        if (config is DefaultConfiguration) {
            val loggerConfiguration = buildConfiguration(
                logToFile,
                initializationLogLevel,
            )

            Configurator.initialize(loggerConfiguration)
        }

        return LogManager.getRootLogger()
    }

    /**
     * Builds the Log4J2 configuration.
     */
    private fun buildConfiguration(
        logToFile: Boolean,
        initializationLogLevel: Severity,
    ): Configuration {
        val builder = ConfigurationBuilderFactory.newConfigurationBuilder()
        val rootLogger: RootLoggerComponentBuilder = builder.newRootLogger(Level.INFO)

        builder.setStatusLevel(initializationLogLevel.toLevel())

        val consoleAppender = createConsoleAppender(builder)
        builder.add(consoleAppender)
        rootLogger.add(builder.newAppenderRef(consoleAppender.name))

        if (logToFile) {
            val fileAppender = createFileAppender(builder)
            builder.add(fileAppender)
            rootLogger.add(builder.newAppenderRef(fileAppender.name))
        }

        builder.add(rootLogger)
        builder.writeXmlConfiguration(System.out)
        println()
        return builder.build()
    }

    private fun createConsoleAppender(builder: ConfigurationBuilder<BuiltConfiguration>): AppenderComponentBuilder {
        // set the pattern layout and pattern
        val layoutBuilder: LayoutComponentBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", LOG_PATTERN)

        return builder.newAppender("Console", "Console")
            .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
            .add(layoutBuilder)
    }

    private fun createFileAppender(builder: ConfigurationBuilder<BuiltConfiguration>): AppenderComponentBuilder {
        // set the pattern layout and pattern
        val layoutBuilder: LayoutComponentBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", LOG_PATTERN)

        // specifying the policy for rolling file
        val triggeringPolicy = builder.newComponent("Policies")
            .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10MB"))

        // create a console appender
        return builder.newAppender("LogToRollingFile", "RollingFile")
            .addAttribute("fileName", LoggerJVM.FILENAME)
            .addAttribute("filePattern", "${LoggerJVM.FILENAME}-%d{MM-dd-yy-HH-mm-ss}.log.")
            .add(layoutBuilder)
            .addComponent(triggeringPolicy)
    }

    private const val LOG_PATTERN = "%d\t%p\t[%t]\t%m%n"
}
