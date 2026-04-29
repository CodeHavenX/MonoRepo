package com.cramsan.architecture.client.manager

import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.configuration.PropertyValue
import com.cramsan.framework.configuration.PropertyValueType
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Represents an event emitted by [PreferencesManager].
 */
sealed interface PreferencesEvent {
    /** A single preference key was modified or removed. */
    data class KeyModified(val key: SettingKey<*>) : PreferencesEvent

    /** All preferences were cleared. */
    data object PreferencesCleared : PreferencesEvent
}

/**
 * Manager for handling user preferences.
 *
 * This manager provides methods to set and load preferences, and emits [PreferencesEvent]s when
 * preferences change. This class will work as a high-level interface for managing user preferences
 * in the application, while implemented using a [Preferences] under the hood.
 *
 * @property settingsHolder The [SettingsHolder] instance for accessing application settings.
 * @property dependencies The [ManagerDependencies] instance providing necessary dependencies.
 */
@FrontendManager
class PreferencesManager(private val settingsHolder: SettingsHolder, private val dependencies: ManagerDependencies) {
    private val _events = MutableSharedFlow<PreferencesEvent>()
    val events: SharedFlow<PreferencesEvent>
        get() = _events.asSharedFlow()

    /**
     * Update a preference with the given key and value.
     * @param key The [SettingKey] representing the preference to update.
     * @param value The [PropertyValue] to set for the given key.
     */
    suspend fun updatePreference(key: SettingKey<*>, value: PropertyValue) =
        dependencies.getOrCatch(TAG) {
            settingsHolder.saveValue(key, value)
            _events.emit(PreferencesEvent.KeyModified(key))
        }

    /**
     * Update a String, Boolean, Int, or Long preference with the given key and value.
     * @param key The [SettingKey] representing the preference to update.
     * @param value The value to set for the given key.
     */
    suspend fun updatePreference(
        key: SettingKey<PropertyValueType.StringType>,
        value: String,
    ) = dependencies.getOrCatch(
        TAG,
    ) {
        settingsHolder.saveString(key, value)
        _events.emit(PreferencesEvent.KeyModified(key))
    }

    /**
     * Update a String, Boolean, Int, or Long preference with the given key and value.
     * @param key The [SettingKey] representing the preference to update.
     * @param value The value to set for the given key.
     */
    suspend fun updatePreference(
        key: SettingKey<PropertyValueType.BooleanType>,
        value: Boolean,
    ) = dependencies.getOrCatch(
        TAG,
    ) {
        settingsHolder.saveBoolean(key, value)
        _events.emit(PreferencesEvent.KeyModified(key))
    }

    /**
     * Update a String, Boolean, Int, or Long preference with the given key and value.
     * @param key The [SettingKey] representing the preference to update.
     * @param value The value to set for the given key.
     */
    suspend fun updatePreference(key: SettingKey<PropertyValueType.IntType>, value: Int) =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.saveInt(key, value)
            _events.emit(PreferencesEvent.KeyModified(key))
        }

    /**
     * Update a String, Boolean, Int, or Long preference with the given key and value.
     * @param key The [SettingKey] representing the preference to update.
     * @param value The value to set for the given key.
     */
    suspend fun updatePreference(key: SettingKey<PropertyValueType.LongType>, value: Long) =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.saveLong(key, value)
            _events.emit(PreferencesEvent.KeyModified(key))
        }

    /**
     * Get the preference value for the given key.
     * @param key The [SettingKey] representing the preference to retrieve.
     * @return A [Result] containing the [PropertyValue] if found, or null if not set.
     */
    suspend fun getPreferenceValue(key: SettingKey<*>): Result<PropertyValue?> =
        dependencies.getOrCatch(TAG) {
            settingsHolder.getValue(key)
        }

    /**
     * Get a String, Boolean, Int, or Long preference with the given key.
     * @param key The [SettingKey] representing the preference to retrieve.
     * @return A [Result] containing the value if found, or null if not set.
     */
    suspend fun getStringPreference(
        key: SettingKey<PropertyValueType.StringType>,
    ): Result<String?> =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.getString(key)
        }

    /**
     * Get a String, Boolean, Int, or Long preference with the given key.
     * @param key The [SettingKey] representing the preference to retrieve.
     * @return A [Result] containing the value if found, or null if not set.
     */
    suspend fun getBooleanPreference(
        key: SettingKey<PropertyValueType.BooleanType>,
    ): Result<Boolean?> =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.getBoolean(key)
        }

    /**
     * Get a String, Boolean, Int, or Long preference with the given key.
     * @param key The [SettingKey] representing the preference to retrieve.
     * @return A [Result] containing the value if found, or null if not set.
     */
    suspend fun getIntPreference(key: SettingKey<PropertyValueType.IntType>): Result<Int?> =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.getInt(key)
        }

    /**
     * Get a String, Boolean, Int, or Long preference with the given key.
     * @param key The [SettingKey] representing the preference to retrieve.
     * @return A [Result] containing the value if found, or null if not set.
     */
    suspend fun getLongPreference(key: SettingKey<PropertyValueType.LongType>): Result<Long?> =
        dependencies.getOrCatch(
            TAG,
        ) {
            settingsHolder.getLong(key)
        }

    /**
     * Remove a preference with the given key.
     * @param key The [SettingKey] representing the preference to remove.
     */
    suspend fun removePreference(key: SettingKey<*>) =
        dependencies.getOrCatch(TAG) {
            settingsHolder.cleanPreference(key)
            _events.emit(PreferencesEvent.KeyModified(key))
        }

    /**
     * Clear all preferences. Emits [PreferencesEvent.PreferencesCleared] to notify observers.
     */
    suspend fun clearPreferences() =
        dependencies.getOrCatch(TAG) {
            settingsHolder.clearAllPreferences()
            _events.emit(PreferencesEvent.PreferencesCleared)
        }

    companion object {
        const val TAG = "PreferencesManager"
    }
}
