package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.managers.PreferencesManager
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.edifikana.client.lib.toSelectedTheme
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
    EdifikanaApplicationUIState.Initial,
    TAG
) {

    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        viewModelScope.launch {
            initHandler.startStep()

            viewModelScope.launch {
                preferences.modifiedKey.collect { changedKey ->
                    when (changedKey) {
                        EdifikanaSettingKey.SelectedTheme -> {
                            logI(TAG, "Theme mode preference changed, emitting event.")
                            loadSelectedThemeSetting()
                        }

                        EdifikanaSettingKey.OpenDebugWindow -> {
                            logI(TAG, "Debug window preference changed, emitting event.")
                            loadDebugWindowSettings()
                        }
                    }
                }
            }
            loadAllSettings()
            updateUiState {
                it.copy(applicationLoaded = true)
            }
        }
    }

    private suspend fun loadAllSettings() {
        loadSelectedThemeSetting()
        loadDebugWindowSettings()
    }

    private suspend fun loadSelectedThemeSetting() {
        val themeMode = preferences.selectedTheme().getOrThrow()
        updateUiState {
            it.copy(
                theme = themeMode.toSelectedTheme(),
            )
        }
    }

    private suspend fun loadDebugWindowSettings() {
        val showDebugWindow = preferences.isOpenDebugWindow().getOrThrow()
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

    companion object {
        private const val TAG = "EdifikanaProcessViewModel"
    }
}
