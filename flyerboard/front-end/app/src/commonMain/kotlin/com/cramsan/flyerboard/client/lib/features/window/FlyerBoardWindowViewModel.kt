package com.cramsan.flyerboard.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import com.cramsan.flyerboard.client.lib.managers.AuthManager
import com.cramsan.flyerboard.client.lib.managers.UserManager
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.framework.annotations.FrontendViewModel
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
@FrontendViewModel
class FlyerBoardWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<FlyerBoardWindowDelegatedEvent>,
    private val authManager: AuthManager,
    private val userManager: UserManager,
) : BaseViewModel<FlyerBoardWindowViewModelEvent, FlyerBoardWindowUIState>(
    dependencies,
    FlyerBoardWindowUIState.Initial,
    TAG,
) {
    init {
        viewModelCoroutineScope.launch {
            windowEventEmitter.events.collect { event ->
                logI(TAG, "Window event received: $event")
                emitEvent(
                    FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                        event as FlyerBoardWindowsEvent,
                    ),
                )
            }
        }
        viewModelCoroutineScope.launch {
            // This line is used to do an initial load of the auth library
            authManager.isAuthenticated()
            authManager.activeUser().collect { userId ->
                val authState =
                    if (userId == null) {
                        logI(TAG, "Auth state changed: unauthenticated")
                        AuthState.Unauthenticated
                    } else {
                        val isAdmin = userManager.getCurrentUser().getOrNull()?.role == UserRole.ADMIN
                        logI(TAG, "Auth state changed: authenticated, isAdmin=$isAdmin")
                        AuthState.Authenticated(isAdmin = isAdmin)
                    }
                updateUiState { it.copy(authState = authState) }
            }
        }
    }

    /**
     * Sign out the current user and navigate to the main nav graph (public browse).
     */
    fun signOut() {
        viewModelCoroutineScope.launch {
            logI(TAG, "signOut")
            authManager.signOut()
            emitEvent(
                FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                    FlyerBoardWindowsEvent.NavigateToNavGraph(
                        destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                        clearStack = true,
                    ),
                ),
            )
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelCoroutineScope.launch {
            logI(TAG, "Result from snackbar: $result")
            delegatedEvents.push(FlyerBoardWindowDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    companion object {
        private const val TAG = "FlyerBoardWindowViewModel"
    }
}
