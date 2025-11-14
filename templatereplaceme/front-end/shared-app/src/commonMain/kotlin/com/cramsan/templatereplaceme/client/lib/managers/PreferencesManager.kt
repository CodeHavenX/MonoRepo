package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.preferences.Preferences
import com.cramsan.templatereplaceme.client.lib.settings.TemplateReplaceMeSettingKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manager for handling user preferences.
 *
 * This manager provides methods to set and load preferences, and emits events when a preference is modified.
 * This class will work as a high-level interface for managing user preferences in the application, while implemented
 * using a [Preferences] under the hood.
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
     * Get the BE override URL.
     * Returns a default URL if not set.
     */
    suspend fun getTemplateReplaceMeBackendUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) ?: "http://0.0.0.0:9292"
    }

    /**
     * Determine if the debug window should be opened.
     * Returns false by default if not set.
     */
    suspend fun isOpenDebugWindow(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        settingsHolder.getBoolean(TemplateReplaceMeSettingKey.OpenDebugWindow) ?: false
    }

    /**
     * Get the logging severity override.
     */
    suspend fun loggingSeverityOverride(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getString(FrontEndApplicationSettingKey.LoggingLevel) ?: "DEBUG"
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
