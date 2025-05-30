package com.cramsan.edifikana.client.lib.managers

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

    companion object {
        const val TAG = "PreferencesManager"
    }
}
