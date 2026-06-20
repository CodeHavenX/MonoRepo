package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

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
 * ViewModel for the Flyer Detail screen.
 */
@FrontendViewModel
class FlyerDetailViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<FlyerDetailEvent, FlyerDetailUIState>(dependencies, FlyerDetailUIState.Initial, TAG) {
    private var currentFlyerId: FlyerId? = null

    /**
     * Load the flyer identified by [flyerIdValue].
     */
    fun loadFlyer(flyerIdValue: String) {
        logI(TAG, "loadFlyer: $flyerIdValue")
        currentFlyerId = FlyerId(flyerIdValue)
        viewModelCoroutineScope.launch {
            updateUiState { FlyerDetailUIState.Loading }
            flyerManager
                .getFlyer(FlyerId(flyerIdValue))
                .onSuccess { flyer ->
                    if (flyer == null) {
                        updateUiState { FlyerDetailUIState.NotFound }
                        emitWindowEvent(
                            FlyerBoardWindowsEvent.ShowSnackbar(
                                message = "Flyer not found.",
                            ),
                        )
                    } else {
                        updateUiState { FlyerDetailUIState.Content(flyer) }
                    }
                }.onFailure { error ->
                    updateUiState { FlyerDetailUIState.NotFound }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load flyer: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Navigate to the edit screen for the current flyer.
     */
    fun editFlyer() {
        val flyerId = currentFlyerId ?: return
        logI(TAG, "editFlyer: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerEditDestination(flyerId.flyerId),
                ),
            )
        }
    }

    /**
     * Approve the current flyer and navigate back.
     */
    fun approveFlyer() {
        val flyerId = currentFlyerId ?: return
        logI(TAG, "approveFlyer: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            flyerManager
                .moderate(flyerId, ACTION_APPROVE)
                .onSuccess {
                    emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer approved."))
                    emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
                }.onFailure { error ->
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to approve flyer: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reject the current flyer and navigate back.
     */
    fun rejectFlyer() {
        val flyerId = currentFlyerId ?: return
        logI(TAG, "rejectFlyer: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            flyerManager
                .moderate(flyerId, ACTION_REJECT)
                .onSuccess {
                    emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer rejected."))
                    emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
                }.onFailure { error ->
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to reject flyer: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Navigate back to the flyer list.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "FlyerDetailViewModel"
        private const val ACTION_APPROVE = "approve"
        private const val ACTION_REJECT = "reject"
    }
}
