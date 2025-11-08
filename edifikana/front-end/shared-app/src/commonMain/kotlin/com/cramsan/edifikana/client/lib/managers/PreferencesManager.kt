package com.cramsan.edifikana.client.lib.managers

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manager for handling user preferences.
 *
 * This manager provides methods to set and load preferences, and emits events when a preference is modified.
 * This class will work as a high-level interface for managing user preferences in the application, while implemented
 * using a [SettingsHolder] under the hood.
 *
 * @property settingsHolder The [SettingsHolder] instance for accessing application settings.
 * @property dependencies The [ManagerDependencies] instance providing necessary dependencies.
 */
class PreferencesManager(
    private val settingsHolder: SettingsHolder,
    private val dependencies: ManagerDependencies,
) {
    private val _modifiedKey = MutableSharedFlow<SettingKey<*>>()
    val modifiedKey: SharedFlow<SettingKey<*>>
        get() = _modifiedKey.asSharedFlow()

    /**
     * Determine if we should halt the current thread when an assert fails.
     */
    suspend fun haltOnFailure(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        settingsHolder.getBoolean(FrontEndApplicationSettingKey.HaltOnFailure) ?: false
    }

    /**
     * Update a preference with the given key and value.
     *
     * @param key The [SettingKey] representing the preference to update.
     * @param value The [PropertyValue] to set for the given key.
     */
    suspend fun updatePreference(key: SettingKey<*>, value: PropertyValue) = dependencies.getOrCatch(TAG) {
        settingsHolder.saveValue(key, value)
        _modifiedKey.emit(key)
    }

    /**
     * Get the Supabase override URL.
     * Returns a default URL if not set.
     */
    suspend fun getSupabaseOverrideUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideUrl).orEmpty()
    }

    /**
     * Get the Supabase override key.
     * Returns an empty string if not set.
     */
    suspend fun getSupabaseOverrideKey(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideKey).orEmpty()
    }

    /**
     * Get the Edifikana backend URL.
     * Returns a default URL if not set.
     */
    suspend fun getEdifikanaBackendUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(EdifikanaSettingKey.EdifikanaBeUrl).orEmpty()
    }

    /**
     * Determine if the debug window should be opened.
     * Returns false by default if not set.
     */
    suspend fun isOpenDebugWindow(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        settingsHolder.getBoolean(EdifikanaSettingKey.OpenDebugWindow) ?: false
    }

    /**
     * Get the logging severity override.
     * Returns "INFO" by default if not set.
     */
    suspend fun loggingSeverityOverride(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(FrontEndApplicationSettingKey.LoggingLevel) ?: "INFO"
    }

    /**
     * Clear all preferences.
     */
    suspend fun clearPreferences() = dependencies.getOrCatch(TAG) {
        settingsHolder.clearAllPreferences()
    }

    companion object {
        const val TAG = "PreferencesManager"
    }
}
