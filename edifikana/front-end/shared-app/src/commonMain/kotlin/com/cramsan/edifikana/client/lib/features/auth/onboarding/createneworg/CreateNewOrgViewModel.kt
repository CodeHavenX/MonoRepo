package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the CreateNewOrg screen.
 */
class CreateNewOrgViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<CreateNewOrgEvent, CreateNewOrgUIState>(
    dependencies,
    CreateNewOrgUIState.Initial,
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
     * Update the organization name value.
     */
    fun onOrganizationNameChanged(name: String) {
        viewModelScope.launch {
            updateUiState { it.copy(organizationName = name) }
        }
    }

    /**
     * Update the organization description value.
     */
    fun onOrganizationDescriptionChanged(description: String) {
        viewModelScope.launch {
            updateUiState { it.copy(organizationDescription = description) }
        }
    }

    /**
     * Handle the create organization button click.
     */
    fun onCreateOrganizationClicked() {
        logI(TAG, "Create organization clicked")
        // TODO: Implement organization creation logic
    }

    companion object {
        private const val TAG = "CreateNewOrgViewModel"
    }
}
