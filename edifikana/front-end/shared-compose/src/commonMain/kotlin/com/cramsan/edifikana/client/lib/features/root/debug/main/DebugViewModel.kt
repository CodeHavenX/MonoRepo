package com.cramsan.edifikana.client.lib.features.root.debug.main

import com.cramsan.edifikana.client.lib.di.koin.DEBUG_KEY
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.cramsan.framework.preferences.Preferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Debug screen.
 **/
class DebugViewModel(
    dependencies: ViewModelDependencies,
    private val preferences: Preferences,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(DebugUIState(DebugUIModel(emptyList())))

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<DebugUIState> = _uiState

    private val _event = MutableSharedFlow<DebugEvent>()

    /**
     * Load the initial state of the screen.
     */
    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                content = DebugUIModel(
                    listOf(
                        Field.BooleanField(
                            key = DEBUG_KEY,
                            value = preferences.loadBoolean(DEBUG_KEY) ?: DEBUG_INITIAL_VALUE,
                        ),
                    ),
                ),
            )
        }
    }

    /**
     * Save the value of the field.
     */
    fun saveValue(key: String, value: Any) {
        when (value) {
            is String -> preferences.saveString(key, value)
            is Boolean -> preferences.saveBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported value type: $value with key $key")
        }
        handleFieldChange(key, value)
        load()
    }

    private fun handleFieldChange(key: String, value: Any) {
        when (key) {
            DEBUG_KEY -> {
                logI(TAG, "Debug key changed to: $value")
            }
            else -> {
                logW(TAG, "Unsupported key: $key")
            }
        }
    }

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<DebugEvent> = _event

    companion object {
        private const val TAG = "DebugViewModel"

        private const val DEBUG_INITIAL_VALUE = false
    }
}
