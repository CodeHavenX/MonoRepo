package com.cramsan.framework.sample.shared.features.main.preferences

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

private const val KEY_STRING = "sample_key_string"
private const val KEY_INT = "sample_key_int"
private const val KEY_LONG = "sample_key_long"
private const val KEY_BOOL = "sample_key_bool"
private const val SAMPLE_INT_VALUE = 42
private const val SAMPLE_LONG_VALUE = 100L

/**
 * ViewModel for the Preferences screen.
 */
@FrontendViewModel
class PreferencesViewModel(dependencies: ViewModelDependencies, private val preferences: Preferences) :
    BaseViewModel<PreferencesEvent, PreferencesUIState>(
        dependencies,
        PreferencesUIState.Initial,
        TAG,
    ) {
    /**
     * Save a hardcoded string value to preferences.
     */
    fun saveString() {
        preferences.saveString(KEY_STRING, "hello")
    }

    /**
     * Load the string value from preferences and update the UI state.
     */
    fun loadString() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(stringValue = preferences.loadString(KEY_STRING)) }
        }
    }

    /**
     * Save a hardcoded int value to preferences.
     */
    fun saveInt() {
        preferences.saveInt(KEY_INT, SAMPLE_INT_VALUE)
    }

    /**
     * Load the int value from preferences and update the UI state.
     */
    fun loadInt() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(intValue = preferences.loadInt(KEY_INT)) }
        }
    }

    /**
     * Save a hardcoded long value to preferences.
     */
    fun saveLong() {
        preferences.saveLong(KEY_LONG, SAMPLE_LONG_VALUE)
    }

    /**
     * Load the long value from preferences and update the UI state.
     */
    fun loadLong() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(longValue = preferences.loadLong(KEY_LONG)) }
        }
    }

    /**
     * Save a hardcoded boolean value to preferences.
     */
    fun saveBoolean() {
        preferences.saveBoolean(KEY_BOOL, true)
    }

    /**
     * Load the boolean value from preferences and update the UI state.
     */
    fun loadBoolean() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(booleanValue = preferences.loadBoolean(KEY_BOOL)) }
        }
    }

    /**
     * Remove the string key from preferences.
     */
    fun remove() {
        preferences.remove(KEY_STRING)
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(stringValue = null) }
        }
    }

    /**
     * Clear all preferences and reset displayed values.
     */
    fun clear() {
        preferences.clear()
        viewModelCoroutineScope.launch {
            updateUiState { PreferencesUIState.Initial }
        }
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "PreferencesViewModel"
    }
}
