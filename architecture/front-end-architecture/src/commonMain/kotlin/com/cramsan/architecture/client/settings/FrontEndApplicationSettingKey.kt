package com.cramsan.architecture.client.settings

/**
 * Object holding the application setting keys for the front-end.
 */
object FrontEndApplicationSettingKey {
    /**
     * Key for the logging level setting.
     */
    val LoggingLevel = SettingKey.string("logging.level")

    /**
     * Key for enabling file logging.
     */
    val LoggingEnableFileLogging = SettingKey.boolean("logging.enable_file_logging")

    /**
     * Key for halting on failure setting.
     */
    val HaltOnFailure = SettingKey.boolean("halt_on_failure")

    /**
     * Key for the back-end URL setting.
     */
    val BackEndUrl = SettingKey.string("backend.url")

    /**
     * Key for controlling debug-mode logging. When true, verbose ViewModel logs are emitted.
     * Defaults to the platform build type if not explicitly set.
     */
    val IsDebug = SettingKey.boolean("is_debug")
}
