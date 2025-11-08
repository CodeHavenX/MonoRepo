package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.preferences.Preferences
import com.cramsan.templatereplaceme.client.lib.ClientSettingsHolder
import com.cramsan.templatereplaceme.client.lib.PropertyKey
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
 * @property settingsHolder The [ClientSettingsHolder] instance for accessing client settings.
 * @property dependencies The [ManagerDependencies] instance providing necessary dependencies.
 */
class PreferencesManager(
    private val preferences: Preferences,
    private val settingsHolder: ClientSettingsHolder,
    private val dependencies: ManagerDependencies,
) {
    private val _modifiedKey = MutableSharedFlow<PropertyKey>()
    val modifiedKey: SharedFlow<PropertyKey>
        get() = _modifiedKey.asSharedFlow()

    /**
     * Sets a preference value for the given key.
     */
    suspend fun setPreference(key: PropertyKey, value: Any) = dependencies.getOrCatch(TAG) {
        when (value) {
            is String -> preferences.saveString(key.key, value)
            is Boolean -> preferences.saveBoolean(key.key, value)
            else -> throw IllegalArgumentException("Unsupported value type: $value with key $key")
        }
        _modifiedKey.emit(key)
    }

    /**
     * Determine if we should halt the current thread when an assert fails.
     */
    suspend fun haltOnFailure(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        settingsHolder.haltOnFailure()
    }

    /**
     * Get the BE override URL.
     * Returns a default URL if not set.
     */
    suspend fun getTemplateReplaceMeBackendUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getTemplateReplaceMeBackendUrl()
    }

    /**
     * Determine if the debug window should be opened.
     * Returns false by default if not set.
     */
    suspend fun isOpenDebugWindow(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        settingsHolder.isOpenDebugWindow()
    }

    /**
     * Get the logging severity override.
     */
    suspend fun loggingSeverityOverride(): Result<String> = dependencies.getOrCatch(TAG) {
        settingsHolder.getLoggingSeverity()
    }

    /**
     * Clear all preferences.
     */
    suspend fun clearPreferences() = dependencies.getOrCatch(TAG) {
        preferences.clear()
    }

    companion object {
        const val TAG = "PreferencesManager"
    }
}
