package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the JoinOrganization screen.
 */
class JoinOrganizationViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<JoinOrganizationEvent, JoinOrganizationUIState>(
    dependencies,
    JoinOrganizationUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Update the organization name or invite code value.
     */
    fun onOrganizationNameOrCodeChanged(value: String) {
        viewModelScope.launch {
            updateUiState { it.copy(organizationNameOrCode = value) }
        }
    }

    /**
     * Handle the join organization button click.
     */
    fun onJoinOrganizationClicked() {
        logI(TAG, "Join organization clicked")
        // TODO: Implement join organization logic
    }

    /**
     * Navigate to create new workspace screen.
     */
    fun onCreateNewWorkspaceClicked() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.CreateNewOrgDestination))
        }
    }

    companion object {
        private const val TAG = "JoinOrganizationViewModel"
    }
}
