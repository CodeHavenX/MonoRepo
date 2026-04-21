package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.flyerboard.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * View model for the entire window.
 */
class FlyerBoardWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<FlyerBoardWindowDelegatedEvent>,
    private val authManager: AuthManager,
) : BaseViewModel<FlyerBoardWindowViewModelEvent, FlyerBoardWindowUIState>(
    dependencies,
    FlyerBoardWindowUIState.Initial,
    TAG
) {

    init {
        viewModelScope.launch {
            windowEventEmitter.events.collect { event ->
                logI(TAG, "Window event received: $event")
                emitEvent(
                    FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                        event as FlyerBoardWindowsEvent
                    )
                )
            }
        }
        viewModelScope.launch {
            authManager.activeUser().collect { userId ->
                logI(TAG, "Auth state changed: ${if (userId != null) "authenticated" else "unauthenticated"}")
                updateUiState { it.copy(isAuthenticated = userId != null) }
            }
        }
    }

    /**
     * Sign out the current user and navigate to the main nav graph (public browse).
     */
    fun signOut() {
        viewModelScope.launch {
            logI(TAG, "signOut")
            authManager.signOut()
            emitEvent(
                FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                    FlyerBoardWindowsEvent.NavigateToNavGraph(
                        destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                        clearStack = true,
                    )
                )
            )
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            delegatedEvents.push(FlyerBoardWindowDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "FlyerBoardWindowViewModel"
    }
}
