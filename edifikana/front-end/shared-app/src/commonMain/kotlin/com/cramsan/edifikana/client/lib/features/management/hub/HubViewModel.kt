package com.cramsan.edifikana.client.lib.features.management.hub

import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Hub screen.
 **/
class HubViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<HubEvent, HubUIModel>(
    dependencies,
    HubUIModel.Empty,
    TAG,
) {
    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.AccountNavGraphDestination)
            )
        }
    }

    /**
     * Set the selected tab.
     */
    fun selectTab(selectedTab: Tabs) {
        viewModelScope.launch {
            updateUiState { it.copy(selectedTab = selectedTab) }
        }
    }

    /**
     * Navigate to the notifications screen.
     */
    fun navigateToNotifications() {
        logI(TAG, "Navigate to the notifications screen")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AccountDestination.NotificationsDestination,
                )
            )
        }
    }

    companion object {
        private const val TAG = "HubViewModel"
    }
}
