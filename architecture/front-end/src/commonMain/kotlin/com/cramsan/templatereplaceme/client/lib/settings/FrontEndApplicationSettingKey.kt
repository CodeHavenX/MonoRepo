package com.cramsan.templatereplaceme.client.lib.settings


object FrontEndApplicationSettingKey {
    val LoggingLevel = SettingKey.string("logging.level")
    val LoggingEnableFileLogging= SettingKey.boolean("logging.enable_file_logging")
    val HaltOnFailure = SettingKey.boolean("halt_on_failure", )
    val BackEndUrl = SettingKey.string("backend.url")
}
