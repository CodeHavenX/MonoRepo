package com.cramsan.edifikana.client.lib.features.root.account.account

import com.cramsan.edifikana.client.lib.features.root.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Account screen view model.
 */
class AccountViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        AccountUIState(
            content = AccountUIModel(""),
            isLoading = false,
        )
    )

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<AccountUIState> = _uiState

    private val _event = MutableSharedFlow<AccountEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<AccountEvent> = _event

    /**
     * Sign out and navigate out of this screen.
     */
    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _event.emit(
                AccountEvent.TriggerEdifikanaApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AuthDestination)
                )
            )
        }
    }
}
