package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.flyerboard.client.lib.init.Initializer
import kotlinx.coroutines.launch

/**
 * View model for the entire application.
 */
class FlyerBoardApplicationViewModel(
    private val initHandler: Initializer,
    dependencies: ViewModelDependencies,
) : BaseViewModel<FlyerBoardApplicationViewModelEvent, FlyerBoardApplicationUIState>(
    dependencies,
    FlyerBoardApplicationUIState(),
    TAG
) {
    /**
     * Initialize the view model and all required state for the entire application.
     */
    fun initialize() {
        initHandler.startStep()
    }

    /**
     * Set whether to show the debug window.
     */
    fun setShowDebugWindow(show: Boolean) {
        viewModelScope.launch {
            updateUiState {
                it.copy(showDebugWindow = show)
            }
        }
    }

    companion object {
        private const val TAG = "FlyerBoardApplicationViewModel"
    }
}
