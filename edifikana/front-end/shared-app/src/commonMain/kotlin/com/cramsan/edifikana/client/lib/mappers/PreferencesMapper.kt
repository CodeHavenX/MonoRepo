package com.cramsan.edifikana.client.lib.mappers

import com.cramsan.edifikana.client.lib.di.DEFAULT_BE_URL
import com.cramsan.edifikana.client.lib.di.DEFAULT_SUPABASE_URL
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.preferences.Preferences

/**
 * Mapper for retrieving preferences related to the Edifikana application.
 *
 * This class provides utility methods to access various settings and overrides
 * that can be configured in the application. By using this mapper we can provide a single
 * source of truth for the preferences, allowing it to be invoked from multiple entry points.
 */
object PreferencesMapper {

    /**
     * Utility methods to retrieve if we should connect to supabase.
     */
    fun isSupabaseDisabled(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_DISABLE_SUPABASE.name) ?: false
    }

    /**
     * Utility methods to retrieve if we should halt on assert failure.
     */
    fun haltOnFailure(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_HALT_ON_FAILURE.name) ?: false
    }

    /**
     * Utility methods to retrieve the URL and key for the Supabase override.
     */
    fun getSupabaseOverrideUrl(preferences: Preferences): String {
        return preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_URL.name) ?: DEFAULT_SUPABASE_URL
    }

    /**
     * Utility methods to retrieve the key for the Supabase override.
     * Returns an empty string if not set.
     */
    fun getSupabaseOverrideKey(preferences: Preferences): String {
        return preferences.loadString(Overrides.KEY_SUPABASE_OVERRIDE_KEY.name) ?: ""
    }

    /**
     * Utility methods to check if the Supabase override is enabled.
     * Returns true by default if not set.
     */
    fun isSupabaseOverrideEnabled(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_SUPABASE_OVERRIDE_ENABLED.name) ?: true
    }

    /**
     * Utility methods to check if the backend should be disabled.
     */
    fun isBackendDisabled(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_DISABLE_BE.name) ?: false
    }

    /**
     * Utility methods to retrieve the Edifikana backend URL.
     * Returns a default URL if not set.
     */
    fun getEdifikanaBackendUrl(preferences: Preferences): String {
        return preferences.loadString(Overrides.KEY_EDIFIKANA_BE_URL.name) ?: DEFAULT_BE_URL
    }

    /**
     * Utility methods to check if the Edifikana backend override is enabled.
     * Returns true by default if not set.
     */
    fun isEdifikanaBackendOverrideEnabled(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_EDIFIKANA_BE_OVERRIDE_ENABLED.name) ?: true
    }

    /**
     * Utility methods to check if the debug window should be opened.
     */
    fun isOpenDebugWindow(preferences: Preferences): Boolean {
        return preferences.loadBoolean(Overrides.KEY_OPEN_DEBUG_WINDOW.name) ?: false
    }

    /**
     * Utility methods to retrieve the logging severity override.
     * Returns "INFO" by default if not set.
     */
    fun getLoggingSeverityOverride(preferences: Preferences): String {
        return preferences.loadString(Overrides.KEY_LOGGING_SEVERITY_OVERRIDE.name) ?: "INFO"
    }
}
