package com.codehavenx.alpaca.frontend.appcore.features.application

import com.codehavenx.alpaca.frontend.appcore.managers.AuthenticationManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * This ViewModel is used to handle application-wide events and logic.
 */
class ApplicationViewModel(
    private val authenticationManager: AuthenticationManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AlpacaApplicationViewModelEvent, ApplicationUIModelUI>(
    dependencies,
    ApplicationUIModelUI.Initial,
    TAG,
) {

    private val _delegatedEvents = MutableSharedFlow<ApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<ApplicationDelegatedEvent> = _delegatedEvents

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
    fun executeApplicationEvent(event: AlpacaApplicationViewModelEvent) = viewModelScope.launch {
        logI(TAG, "Executing application event: $event")
        emitEvent(event)
    }

    /**
     * Update the sign in status.
     */
    fun setSignInStatus(signedIn: Boolean) {
        viewModelScope.launch {
            logI(TAG, "Setting sign in status to: $signedIn")
            updateUiState {
                if (signedIn) {
                    ApplicationUIModelUI(
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
                    ApplicationUIModelUI(
                        navBar = listOf()
                    )
                }
            }
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
