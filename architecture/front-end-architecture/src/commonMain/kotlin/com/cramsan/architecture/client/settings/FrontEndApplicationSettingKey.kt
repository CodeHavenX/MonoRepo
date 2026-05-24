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

    /**
     * Returns a [SettingGroup] describing all framework-level settings for use with [SettingRegistry].
     *
     * Call this once during DI startup and pass the result to [SettingRegistry.register].
     */
    fun defaultGroup(): SettingGroup =
        settingGroup("Framework") {
        subGroup("Logging") {
            setting(LoggingLevel, "Logging Level", "VERBOSE / DEBUG / INFO / WARNING / ERROR")
            setting(LoggingEnableFileLogging, "Enable File Logging")
        }
        subGroup("Core") {
            setting(HaltOnFailure, "Halt on Failure", "Freezes the app on assertion failure.")
            setting(IsDebug, "Debug Mode Override", "Defaults to the platform build type if unset.")
        }
        subGroup("Network") {
            setting(BackEndUrl, "Back-End URL", "Override the default API base URL.")
        }
    }
}
