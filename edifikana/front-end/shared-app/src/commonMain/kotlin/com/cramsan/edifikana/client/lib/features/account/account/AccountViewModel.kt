package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * Account screen view model.
 */
class AccountViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AccountEvent, AccountUIState>(dependencies, AccountUIState.Empty, TAG) {

    /**
     * Sign out and navigate out of this screen.
     */
    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityRouteDestination.AuthRouteDestination,
                    clearStack = true,
                )
            )
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitApplicationEvent(EdifikanaApplicationEvent.NavigateBack)
        }
    }

    companion object {
        const val TAG = "AccountViewModel"
    }
}
