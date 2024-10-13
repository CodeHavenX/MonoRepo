package com.codehavenx.alpaca.frontend.appcore.features.application

import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.AuthenticationManager
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * This ViewModel is used to handle application-wide events and logic.
 */
class ApplicationViewModel(
    private val authenticationManager: AuthenticationManager,
    workContext: WorkContext,
) : AlpacaViewModel(workContext) {

    private val _events = MutableSharedFlow<ApplicationEvent>()
    val events: SharedFlow<ApplicationEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<ApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<ApplicationDelegatedEvent> = _delegatedEvents

    private val _uiState = MutableStateFlow<ApplicationUIModel>(
        ApplicationUIModel.SignedOut
    )
    val uiState: StateFlow<ApplicationUIModel> = _uiState

    init {
        viewModelScope.launch {
            authenticationManager.userSignInState.collect { signedIn ->
                setSignInStatus(signedIn)
            }
        }
    }

    /**
     * Execute the given [ApplicationEvent].
     */
    fun executeApplicationEvent(event: ApplicationEvent) = viewModelScope.launch {
        logI(TAG, "Executing application event: $event")
        _events.emit(event)
    }

    /**
     * Update the sign in status.
     */
    fun setSignInStatus(signedIn: Boolean) {
        logI(TAG, "Setting sign in status to: $signedIn")
        _uiState.value = if (signedIn) {
            ApplicationUIModel.SignedIn(
                navBar = listOf(
                    NavBarSegment.NavBarItem(
                        name = "Home",
                        path = Route.home(),
                    ),
                    NavBarSegment.NavBarItem(
                        name = "Clients",
                        path = Route.listClients(),
                    ),
                    NavBarSegment.NavBarItem(
                        name = "Staff",
                        path = Route.listStaff(),
                    ),
                    NavBarSegment.NavBarItem(
                        name = "Appointments",
                        path = Route.appointments(),
                    ),
                    NavBarSegment.NavBarItem(
                        name = "Classes and Courses",
                        path = Route.coursesAndClasses(),
                    ),
                )
            )
        } else {
            ApplicationUIModel.SignedOut
        }
    }

    /**
     * Sign out the user.
     */
    fun signOut() {
        viewModelScope.launch {
            authenticationManager.signOut()
        }
    }

    companion object {
        private const val TAG = "ApplicationViewModel"
    }
}
