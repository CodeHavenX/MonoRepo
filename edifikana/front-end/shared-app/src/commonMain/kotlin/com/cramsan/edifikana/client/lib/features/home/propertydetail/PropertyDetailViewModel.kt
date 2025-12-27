package com.cramsan.edifikana.client.lib.features.home.propertydetail

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the PropertyDetail screen.
 **/
class PropertyDetailViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel<PropertyDetailEvent, PropertyDetailUIState>(
    dependencies,
    PropertyDetailUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel with the property ID.
     */
    fun initialize(propertyId: PropertyId) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, propertyId = propertyId) }
            propertyManager.getProperty(propertyId)
                .onSuccess { property ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            name = property.name,
                            address = property.address,
                        )
                    }
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to load property: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
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
     * Toggle edit mode.
     */
    fun toggleEditMode() {
        viewModelScope.launch {
            updateUiState { it.copy(isEditMode = !it.isEditMode) }
        }
    }

    /**
     * Cancel edit mode and revert changes.
     */
    fun cancelEdit() {
        viewModelScope.launch {
            val propertyId = uiState.value.propertyId ?: return@launch
            updateUiState { it.copy(isLoading = true, isEditMode = false) }
            propertyManager.getProperty(propertyId)
                .onSuccess { property ->
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            name = property.name,
                            address = property.address,
                        )
                    }
                }
                .onFailure {
                    updateUiState { it.copy(isLoading = false) }
                }
        }
    }

    /**
     * Update the property name.
     */
    fun onNameChanged(name: String) {
        viewModelScope.launch {
            updateUiState { it.copy(name = name) }
        }
    }

    /**
     * Update the property address.
     */
    fun onAddressChanged(address: String) {
        viewModelScope.launch {
            updateUiState { it.copy(address = address) }
        }
    }

    /**
     * Save the property changes.
     */
    fun saveProperty() {
        viewModelScope.launch {
            val state = uiState.value
            val propertyId = state.propertyId ?: return@launch

            updateUiState { it.copy(isLoading = true) }
            propertyManager.updateProperty(propertyId, state.name, state.address)
                .onSuccess {
                    updateUiState { it.copy(isLoading = false, isEditMode = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Property updated successfully")
                    )
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to update property: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
        }
    }

    /**
     * Delete the property.
     */
    fun deleteProperty() {
        viewModelScope.launch {
            val propertyId = uiState.value.propertyId ?: return@launch

            updateUiState { it.copy(isLoading = true) }
            propertyManager.removeProperty(propertyId)
                .onSuccess {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar("Property deleted successfully")
                    )
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to delete property: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
        }
    }

    companion object {
        private const val TAG = "PropertyDetailViewModel"
    }
}
