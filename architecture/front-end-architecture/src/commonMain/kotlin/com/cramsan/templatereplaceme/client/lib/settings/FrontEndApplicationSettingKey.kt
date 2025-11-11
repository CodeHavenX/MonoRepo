package com.cramsan.templatereplaceme.client.lib.settings

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
    val HaltOnFailure = SettingKey.boolean("halt_on_failure",)

    /**
     * Key for the back-end URL setting.
     */
    val BackEndUrl = SettingKey.string("backend.url")
}
