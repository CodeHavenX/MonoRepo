package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

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
 * ViewModel for the Moderation Queue screen.
 *
 * Loads pending flyers and allows admins to approve or reject them.
 * Non-admin requests are rejected by the backend with an error.
 */
@FrontendViewModel
class ModerationQueueViewModel(dependencies: ViewModelDependencies, private val flyerManager: FlyerManager) :
    BaseViewModel<ModerationQueueEvent, ModerationQueueUIState>(
        dependencies,
        ModerationQueueUIState.Initial,
        TAG,
    ) {
    /**
     * Load the pending flyers awaiting moderation.
     */
    fun loadPendingFlyers() {
        logI(TAG, "loadPendingFlyers")
        viewModelCoroutineScope.launch {
            updateUiState { ModerationQueueUIState.Loading }
            flyerManager
                .listPendingFlyers()
                .onSuccess { paginated ->
                    updateUiState {
                        if (paginated.flyers.isEmpty()) {
                            ModerationQueueUIState.Empty
                        } else {
                            ModerationQueueUIState.Content(flyers = paginated.flyers)
                        }
                    }
                }.onFailure { error ->
                    updateUiState { ModerationQueueUIState.Error }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to load moderation queue: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reload the pending flyers from the beginning.
     */
    fun refresh() {
        logI(TAG, "refresh")
        loadPendingFlyers()
    }

    /**
     * Show the rejection reason dialog for [flyerId].
     */
    fun onRejectTapped(flyerId: FlyerId) {
        logI(TAG, "onRejectTapped: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            updateUiState { state ->
                if (state is ModerationQueueUIState.Content) {
                    state.copy(pendingRejectionFlyerId = flyerId)
                } else {
                    state
                }
            }
        }
    }

    /**
     * Dismiss the rejection reason dialog without rejecting.
     */
    fun onRejectDialogDismissed() {
        logI(TAG, "onRejectDialogDismissed")
        viewModelCoroutineScope.launch {
            updateUiState { state ->
                if (state is ModerationQueueUIState.Content) {
                    state.copy(pendingRejectionFlyerId = null)
                } else {
                    state
                }
            }
        }
    }

    /**
     * Approve the flyer identified by [flyerId] and refresh the list.
     */
    fun approveFlyer(flyerId: FlyerId) {
        logI(TAG, "approveFlyer: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            flyerManager
                .moderate(flyerId, ACTION_APPROVE)
                .onSuccess {
                    emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer approved."))
                    loadPendingFlyers()
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
     * Reject the flyer identified by [flyerId] with an optional [reason] and refresh the list.
     */
    fun rejectFlyer(flyerId: FlyerId, reason: String = "") {
        logI(TAG, "rejectFlyer: ${flyerId.flyerId}")
        viewModelCoroutineScope.launch {
            flyerManager
                .moderate(flyerId, ACTION_REJECT, reason = reason.ifEmpty { null })
                .onSuccess {
                    emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer rejected."))
                    loadPendingFlyers()
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
     * Navigate back.
     */
    fun navigateBack() {
        logI(TAG, "navigateBack")
        viewModelCoroutineScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    /**
     * Navigate to the flyer [id].
     */
    fun navigateToFlyer(id: FlyerId) {
        logI(TAG, "navigateToFlyer")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToScreen(
                    MainDestination.FlyerDetailDestination(id.flyerId),
                ),
            )
        }
    }

    companion object {
        private const val TAG = "ModerationQueueViewModel"
        const val ACTION_APPROVE = "approve"
        const val ACTION_REJECT = "reject"
    }
}
