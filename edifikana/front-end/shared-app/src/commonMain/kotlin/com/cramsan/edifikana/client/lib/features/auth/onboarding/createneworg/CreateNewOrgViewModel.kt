package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the CreateNewOrg screen.
 */
class CreateNewOrgViewModel(
    dependencies: ViewModelDependencies,
    private val organizationManager: OrganizationManager,
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
            validateInputs()
        }
    }

    /**
     * Update the organization description value.
     */
    fun onOrganizationDescriptionChanged(description: String) {
        viewModelScope.launch {
            updateUiState { it.copy(organizationDescription = description) }
            validateInputs()
        }
    }

    private suspend fun validateInputs() {
        val currentState = uiState.value
        val isButtonEnabled =
            currentState.organizationName.isNotBlank()
        updateUiState { it.copy(isButtonEnabled = isButtonEnabled) }
    }

    /**
     * Handle the create organization button click.
     */
    fun onCreateOrganizationClicked() {
        logI(TAG, "Create organization clicked")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            try {
                val currentState = uiState.value
                if (currentState.organizationName.isBlank()) {
                    logI(TAG, "Organization name is blank")
                    return@launch
                }

                organizationManager.createOrganization(
                    name = currentState.organizationName,
                    description = currentState.organizationDescription,
                ).onSuccess {
                    logI(TAG, "Organization created successfully")
                    emitWindowEvent(
                        EdifikanaWindowsEvent.NavigateToNavGraph(
                            EdifikanaNavGraphDestination.HomeNavGraphDestination,
                            clearTop = true,
                        ),
                    )
                }.onFailure { error ->
                    logE(TAG, "Failed to create organization: ${error.message}", error.cause)
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Failed to create organization. Please try again."),
                    )
                }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        private const val TAG = "CreateNewOrgViewModel"
    }
}
