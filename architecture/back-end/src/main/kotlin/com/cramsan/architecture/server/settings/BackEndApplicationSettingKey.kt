package com.cramsan.architecture.server.settings

/**
 * Application setting keys for the Back-End application.
 */
object BackEndApplicationSettingKey {
    val LoggingLevel = SettingKey.string("logging.level")
    val LoggingEnableFileLogging = SettingKey.boolean("logging.enable_file_logging")
    val HaltOnFailure = SettingKey.boolean("halt_on_failure",)
    val AllowedHost = SettingKey.string("allowed.host")
}
