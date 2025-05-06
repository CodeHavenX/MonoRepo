package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleApplicationEvent
import com.cramsan.framework.sample.shared.features.main.MainRouteDestination
import kotlinx.coroutines.launch

/**
 * Main Menu ViewModel.
 */
class MainMenuViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<MainMenuEvent, MainMenuIState>(dependencies, MainMenuIState, TAG) {

    /**
     * Navigate to the HaltUtil screen.
     */
    fun navigateToHaltUtil() {
        viewModelScope.launch {
            emitApplicationEvent(
                SampleApplicationEvent.NavigateToScreen(MainRouteDestination.HaltUtilDestination)
            )
        }
    }

    companion object {
        private const val TAG = "MainMenuViewModel"
    }
}
