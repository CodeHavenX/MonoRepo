package com.cramsan.edifikana.client.lib.features.home.addproperty

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddProperty screen.
 **/
class AddPropertyViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel<AddPropertyEvent, AddPropertyUIState>(
    dependencies,
    AddPropertyUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel with the organization ID.
     */
    fun initialize(orgId: OrganizationId) {
        viewModelScope.launch {
            updateUiState { it.copy(orgId = orgId) }
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Add a new property.
     */
    fun addProperty(propertyName: String, address: String) {
        viewModelScope.launch {
            val organizationId = requireNotNull(uiState.value.orgId)
            updateUiState { it.copy(isLoading = true) }
            val newProperty = propertyManager.addProperty(propertyName, address, organizationId).onFailure {
                updateUiState { it.copy(isLoading = false) }
            }.getOrThrow()

            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar(
                    "Property ${newProperty.name} added successfully"
                )
            )
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "AddPropertyViewModel"
    }
}
