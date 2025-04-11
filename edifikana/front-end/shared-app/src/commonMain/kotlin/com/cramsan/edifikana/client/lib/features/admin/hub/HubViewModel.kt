package com.cramsan.edifikana.client.lib.features.admin.hub

import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
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
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AccountRouteDestination)
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
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(
                    AccountRouteDestination.NotificationsDestination,
                )
            )
        }
    }

    companion object {
        private const val TAG = "HubViewModel"
    }
}
