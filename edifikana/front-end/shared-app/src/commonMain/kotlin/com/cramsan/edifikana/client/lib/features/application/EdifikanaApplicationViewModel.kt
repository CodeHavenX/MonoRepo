package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.managers.PreferencesManager
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
class EdifikanaApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
    private val preferences: PreferencesManager,
) : BaseViewModel<EdifikanaApplicationViewModelEvent, EdifikanaApplicationUIState>(
    dependencies,
    EdifikanaApplicationUIState(),
    TAG
) {

    init {
        viewModelScope.launch {
            preferences.modifiedKey.collect {
                logI(TAG, "Preference key modified: $it")
                loadFromSettings()
            }
        }
    }

    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        viewModelScope.launch {
            viewModelScope.launch {
                initHandler.startStep()
            }

            viewModelScope.launch {
                preferences.modifiedKey.collect {
                    logI(TAG, "Preference key modified: $it")
                    loadFromSettings()
                }
            }

            loadFromSettings()
        }
    }

    private fun loadFromSettings() {
        viewModelScope.launch {
            val showDebugWindow = preferences.loadBooleanPreference(Overrides.KEY_OPEN_DEBUG_WINDOW).getOrThrow()
            if (showDebugWindow) {
                logI(TAG, "Debug window is enabled.")
            } else {
                logI(TAG, "Debug window is disabled.")
            }
            updateUiState {
                it.copy(
                    showDebugWindow = showDebugWindow
                )
            }
        }
    }

    companion object {
        private const val TAG = "EdifikanaProcessViewModel"
    }
}
