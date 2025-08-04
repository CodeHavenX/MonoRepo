package com.cramsan.edifikana.client.lib.features.debug.screenselector

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.coroutines.launch

/**
 * ViewModel for the ScreenSelector screen.
 **/
class ScreenSelectorViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<ScreenSelectorEvent, ScreenSelectorUIState>(
    dependencies,
    ScreenSelectorUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Navigate to a specific destination.
     *
     * @param destination The destination to navigate to.
     */
    fun navigateToDestination(destination: Destination) {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(destination)
            )
        }
    }

    companion object {
        private const val TAG = "ScreenSelectorViewModel"
    }
}
