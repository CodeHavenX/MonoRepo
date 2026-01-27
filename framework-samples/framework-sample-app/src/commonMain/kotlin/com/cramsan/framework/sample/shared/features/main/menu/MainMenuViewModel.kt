package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.features.main.MainDestination
import kotlinx.coroutines.launch

/**
 * Main Menu ViewModel.
 */
class MainMenuViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<MainMenuEvent, MainMenuIState>(dependencies, MainMenuIState, TAG) {

    /**
     * Navigate to the HaltUtil screen.
     */
    fun navigateToHaltUtil() {
        viewModelScope.launch {
            emitWindowEvent(
                SampleWindowEvent.NavigateToScreen(MainDestination.HaltUtilDestination),
            )
        }
    }

    /**
     * Navigate to the Logging screen.
     */
    fun navigateToLogging() {
        viewModelScope.launch {
            emitWindowEvent(
                SampleWindowEvent.NavigateToScreen(MainDestination.LoggingDestination),
            )
        }
    }

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}
