package com.cramsan.templatereplaceme.client.lib

import com.cramsan.framework.logging.Severity
import com.cramsan.framework.preferences.Preferences

/**
 * Mapper for retrieving preferences related to the TemplateReplaceMe application.
 *
 * This class provides utility methods to access various settings and overrides
 * that can be configured in the application. By using this mapper we can provide a single
 * source of truth for the preferences, allowing it to be invoked from multiple entry points.
 */
class ClientSettingsHolder(
    private val preferences: Preferences
) {

    /**
     * Utility methods to retrieve if we should halt on assert failure.
     */
    fun haltOnFailure(): Boolean {
        return preferences.loadBoolean(PropertyKey.HALT_ON_FAILURE.key) ?: false
    }

    /**
     * Utility methods to retrieve the TemplateReplaceMe backend URL.
     * Returns a default URL if not set.
     */
    fun getTemplateReplaceMeBackendUrl(): String {
        return preferences.loadString(PropertyKey.KEY_TEMPLATE_REPLACE_ME_BE_URL.key) ?: DEFAULT_BE_URL
    }

    /**
     * Utility methods to check if the debug window should be opened.
     */
    fun isOpenDebugWindow(): Boolean {
        return preferences.loadBoolean(PropertyKey.OPEN_DEBUG_WINDOW.key) ?: false
    }

    /**
     * Utility methods to retrieve the logging severity.
     * Returns "INFO" by default if not set.
     */
    fun getLoggingSeverity(default: Severity = Severity.DEBUG): String {
        return preferences.loadString(PropertyKey.LOGGING_LEVEL.key) ?: default.name
    }

    /**
     * Utility methods to check if file logging is enabled.
     */
    fun getEnableFileLogging(): Boolean {
        return preferences.loadBoolean(PropertyKey.ENABLE_FILE_LOGGING.key) ?: false
    }
}

const val DEFAULT_BE_URL = "http://127.0.0.1:9292"

/**
 * Enum class representing the keys for various properties.
 */
enum class PropertyKey(val key: String) {
    KEY_TEMPLATE_REPLACE_ME_BE_URL("templatereplaceme.backend_url"),
    LOGGING_LEVEL("logging.level"),
    ENABLE_FILE_LOGGING("logging.enable_file_logging"),
    HALT_ON_FAILURE("halt_on_failure"),
    OPEN_DEBUG_WINDOW("open_debug_window"),
}
