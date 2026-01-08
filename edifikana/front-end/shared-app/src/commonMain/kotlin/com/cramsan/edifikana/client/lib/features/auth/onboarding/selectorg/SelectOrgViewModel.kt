package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the SelectOrg screen.
 */
class SelectOrgViewModel(
    private val authManager: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<SelectOrgEvent, SelectOrgUIState>(
    dependencies,
    SelectOrgUIState,
    TAG,
) {

    /**
     * Load the initial data for the screen.
     */
    fun loadContent() {
        logI(TAG, "Loading SelectOrg content")
    }

    /**
     * Handle join team option click.
     */
    fun joinTeam() {
        logI(TAG, "Join team clicked")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.JoinOrganizationDestination,
                )
            )
        }
    }

    /**
     * Handle create workspace option click.
     */
    fun createWorkspace() {
        logI(TAG, "Create workspace clicked")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.CreateNewOrgDestination,
                )
            )
        }
    }

    /**
     * Request to show sign out confirmation dialog.
     */
    fun requestSignOut() {
        logI(TAG, "Sign out requested")
        viewModelScope.launch {
            emitEvent(SelectOrgEvent.ShowSignOutConfirmation)
        }
    }

    /**
     * Confirm sign out and perform the actual sign out logic.
     */
    fun confirmSignOut() {
        logI(TAG, "Sign out confirmed")
        viewModelScope.launch {
            authManager.signOut()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AuthNavGraphDestination,
                    clearStack = true,
                )
            )
        }
    }

    companion object {
        private const val TAG = "SelectOrgViewModel"
    }
}
