package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.mappers.PreferencesMapper
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.preferences.Preferences
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
 * @property preferences The [Preferences] instance used to store and retrieve user preferences.
 * @property dependencies The [ManagerDependencies] instance providing necessary dependencies.
 */
class PreferencesManager(
    private val preferences: Preferences,
    private val dependencies: ManagerDependencies,
) {
    private val _modifiedKey = MutableSharedFlow<String>()
    val modifiedKey: SharedFlow<String>
        get() = _modifiedKey.asSharedFlow()

    /**
     * Sets a preference value for the given key.
     */
    suspend fun setPreference(key: String, value: Any) = dependencies.getOrCatch(TAG) {
        when (value) {
            is String -> preferences.saveString(key, value)
            is Boolean -> preferences.saveBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported value type: $value with key $key")
        }
        _modifiedKey.emit(key)
    }

    /**
     * Loads a preference value for the given key.
     * Returns an empty string if the preference is not set.
     */
    suspend fun loadStringPreference(key: String): Result<String> = dependencies.getOrCatch(TAG) {
        preferences.loadString(key) ?: ""
    }

    /**
     * Loads a boolean preference value for the given key.
     * Returns false if the preference is not set.
     */
    suspend fun loadBooleanPreference(key: String): Result<Boolean> = dependencies.getOrCatch(TAG) {
        preferences.loadBoolean(key) ?: false
    }

    /**
     * Determine if we should halt the current thread when an assert fails.
     */
    suspend fun haltOnFailure(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.haltOnFailure(preferences)
    }

    /**
     * Get the Supabase override URL.
     * Returns a default URL if not set.
     */
    suspend fun getSupabaseOverrideUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.getSupabaseOverrideUrl(preferences)
    }

    /**
     * Get the Supabase override key.
     * Returns an empty string if not set.
     */
    suspend fun getSupabaseOverrideKey(): Result<String> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.getSupabaseOverrideKey(preferences)
    }

    /**
     * Determine if the Supabase override is enabled.
     * Returns true by default if not set.
     */
    suspend fun isSupabaseOverrideEnabled(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.isSupabaseOverrideEnabled(preferences)
    }

    /**
     * Get the Edifikana backend URL.
     * Returns a default URL if not set.
     */
    suspend fun getEdifikanaBackendUrl(): Result<String> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.getEdifikanaBackendUrl(preferences)
    }

    /**
     * Determine if the Edifikana backend override is enabled.
     * Returns true by default if not set.
     */
    suspend fun isEdifikanaBackendOverrideEnabled(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.isEdifikanaBackendOverrideEnabled(preferences)
    }

    /**
     * Determine if the debug window should be opened.
     * Returns false by default if not set.
     */
    suspend fun isOpenDebugWindow(): Result<Boolean> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.isOpenDebugWindow(preferences)
    }

    /**
     * Get the logging severity override.
     * Returns "INFO" by default if not set.
     */
    suspend fun loggingSeverityOverride(): Result<String> = dependencies.getOrCatch(TAG) {
        PreferencesMapper.getLoggingSeverityOverride(preferences)
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
