package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the My Flyers screen.
 */
@FrontendViewModel
class MyFlyersViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<MyFlyersEvent, MyFlyersUIState>(dependencies, MyFlyersUIState.Initial, TAG) {
    /**
     * Load the authenticated user's own flyers.
     */
    fun loadFlyers() {
        logI(TAG, "loadFlyers")
        viewModelCoroutineScope.launch {
            updateUiState { MyFlyersUIState.Loading }
            flyerManager
                .listMyFlyers()
                .onSuccess { paginated ->
                    updateUiState {
                        if (paginated.flyers.isEmpty()) {
                            MyFlyersUIState.Empty
                        } else {
                            MyFlyersUIState.Content(paginated.flyers)
                        }
                    }
                }.onFailure { error ->
                    updateUiState { MyFlyersUIState.Error }
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
        viewModelCoroutineScope.launch {
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
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerEditDestination(flyerId.flyerId),
                ),
            )
        }
    }

    /**
     * Navigate to the Submit Flyer screen.
     */
    fun onSubmitFlyer() {
        logI(TAG, "onSubmitFlyer")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerSubmitDestination,
                ),
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "MyFlyersViewModel"
    }
}
