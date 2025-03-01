package com.cramsan.edifikana.client.lib.features.admin.hub

import com.cramsan.edifikana.client.lib.features.ActivityDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
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
                EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.AccountDestination)
            )
        }
    }

    /**
     * Set the selected tab.
     */
    fun selectTab(selectedTab: Tabs) {
        updateUiState { it.copy(selectedTab = selectedTab) }
    }

    private suspend fun emitApplicationEvent(applicationEvent: EdifikanaApplicationEvent) {
        emitEvent(
            HubEvent.TriggerApplicationEvent(applicationEvent)
        )
    }

    /**
     * Navigate to the user/home screen.
     */
    fun navigateToUserHome() {
        logI(TAG, "Navigating to user home page.")
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityDestination.MainDestination,
                    clearStack = true,
                )
            )
        }
    }

    companion object {
        private const val TAG = "HubViewModel"
    }
}
