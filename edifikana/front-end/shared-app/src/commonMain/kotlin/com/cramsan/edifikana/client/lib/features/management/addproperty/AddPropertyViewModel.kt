package com.cramsan.edifikana.client.lib.features.management.addproperty

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
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
            updateUiState { it.copy(isLoading = true) }
            val newProperty = propertyManager.addProperty(propertyName, address).onFailure {
                updateUiState { it.copy(isLoading = false) }
                TODO("Handle error when adding property")
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
