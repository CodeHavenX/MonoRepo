package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the Flyer List screen.
 */
class FlyerListViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<FlyerListEvent, FlyerListUIState>(dependencies, FlyerListUIState.Initial, TAG) {

    /**
     * Load the initial page of public flyers.
     */
    fun loadFlyers() {
        logI(TAG, "loadFlyers")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            flyerManager.listFlyers()
                .onSuccess { paginated ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            flyers = paginated.flyers,
                        )
                    }
                }
                .onFailure { error ->
                    updateUiState { it.copy(isLoading = false, errorMessage = error.message) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load flyers: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reload the flyer list from the beginning.
     */
    fun refresh() {
        logI(TAG, "refresh")
        loadFlyers()
    }

    /**
     * Navigate to the detail screen for [flyerId].
     * Wired to TASK-023 (flyer detail screen).
     */
    fun onFlyerSelected(flyerId: FlyerId) {
        logI(TAG, "onFlyerSelected: %s", flyerId.flyerId)
        // Navigation to detail screen will be added in TASK-023.
    }

    companion object {
        private const val TAG = "FlyerListViewModel"
    }
}
