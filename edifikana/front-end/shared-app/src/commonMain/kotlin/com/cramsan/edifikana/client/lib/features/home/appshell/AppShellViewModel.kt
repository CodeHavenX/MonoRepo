package com.cramsan.edifikana.client.lib.features.home.appshell

import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the App Shell screen.
 *
 * Owns the selected tab state and handles navigation to account, settings, and notification screens.
 */
@FrontendViewModel
class AppShellViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<AppShellEvent, AppShellUIState>(
        dependencies,
        AppShellUIState.Initial,
        TAG,
    ) {
    /**
     * Select an app shell tab.
     */
    fun selectTab(tab: AppShellTab) {
        if (tab == AppShellTab.More) {
            navigateToSettings()
            return
        }
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(selectedTab = tab) }
        }
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AccountNavGraphDestination,
                ),
            )
        }
    }

    /**
     * Navigate to the notifications page.
     */
    fun navigateToNotifications() {
        logI(TAG, "Navigating to notifications page.")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(AccountDestination.NotificationsDestination),
            )
        }
    }

    /**
     * Navigate to the settings page.
     */
    fun navigateToSettings() {
        logI(TAG, "Navigating to settings page.")
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.SettingsNavGraphDestination,
                ),
            )
        }
    }

    companion object {
        private const val TAG = "AppShellViewModel"
    }
}
