package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the My Flyers screen.
 */
class MyFlyersViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<MyFlyersEvent, MyFlyersUIState>(dependencies, MyFlyersUIState.Initial, TAG) {

    /**
     * Load the authenticated user's own flyers.
     */
    fun loadFlyers() {
        logI(TAG, "loadFlyers")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            flyerManager.listMyFlyers()
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
                            message = "Failed to load your flyers: ${error.message}",
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
     */
    fun onFlyerSelected(flyerId: FlyerId) {
        logI(TAG, "onFlyerSelected: ${flyerId.flyerId}")
        viewModelScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerDetailDestination(flyerId.flyerId),
                ),
            )
        }
    }

    /**
     * Navigate to the edit screen for [flyerId].
     */
    fun onEditFlyer(flyerId: FlyerId) {
        logI(TAG, "onEditFlyer: ${flyerId.flyerId}")
        viewModelScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerEditDestination(flyerId.flyerId),
                ),
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "MyFlyersViewModel"
    }
}
