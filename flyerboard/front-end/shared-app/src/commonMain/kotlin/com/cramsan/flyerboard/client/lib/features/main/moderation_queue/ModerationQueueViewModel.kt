package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.lib.model.FlyerId
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
class ModerationQueueViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<ModerationQueueEvent, ModerationQueueUIState>(
    dependencies,
    ModerationQueueUIState.Initial,
    TAG,
) {

    /**
     * Load the pending flyers awaiting moderation.
     */
    fun loadPendingFlyers() {
        logI(TAG, "loadPendingFlyers")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            flyerManager.listPendingFlyers()
                .onSuccess { paginated ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            pendingFlyers = paginated.flyers,
                        )
                    }
                }
                .onFailure { error ->
                    updateUiState { it.copy(isLoading = false, errorMessage = error.message) }
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
     * Approve the flyer identified by [flyerId] and refresh the list.
     */
    fun approveFlyer(flyerId: FlyerId) {
        logI(TAG, "approveFlyer: ${flyerId.flyerId}")
        viewModelScope.launch {
            flyerManager.moderate(flyerId, ACTION_APPROVE)
                .onSuccess {
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer approved."),
                    )
                    loadPendingFlyers()
                }
                .onFailure { error ->
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Failed to approve flyer: ${error.message}",
                        ),
                    )
                }
        }
    }

    /**
     * Reject the flyer identified by [flyerId] and refresh the list.
     */
    fun rejectFlyer(flyerId: FlyerId) {
        logI(TAG, "rejectFlyer: ${flyerId.flyerId}")
        viewModelScope.launch {
            flyerManager.moderate(flyerId, ACTION_REJECT)
                .onSuccess {
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(message = "Flyer rejected."),
                    )
                    loadPendingFlyers()
                }
                .onFailure { error ->
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
        viewModelScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "ModerationQueueViewModel"
        const val ACTION_APPROVE = "approve"
        const val ACTION_REJECT = "reject"
    }
}
