package com.cramsan.architecture.server.settings

/**
 * Application setting keys for the Back-End application.
 */
object BackEndApplicationSettingKey {
    /**
     * Key for configuring the logging level (e.g., DEBUG, INFO, WARN, ERROR).
     */
    val LoggingLevel = SettingKey.string("logging.level")

    /**
     * Key for enabling file-based logging output.
     */
    val LoggingEnableFileLogging = SettingKey.boolean("logging.enable_file_logging")

    /**
     * Key for determining whether the application should halt on assertion failures.
     */
    val HaltOnFailure = SettingKey.boolean("halt_on_failure")

    /**
     * Key for specifying the allowed host for CORS configuration.
     */
    val AllowedHost = SettingKey.string("allowed.host")
}
