package com.cramsan.edifikana.client.lib.features.admin.properties

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the PropertyManager screen.
 **/
class PropertyManagerViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel<PropertyManagerEvent, PropertyManagerUIState>(dependencies, PropertyManagerUIState.Empty, TAG) {

    /**
     * Load the page.
     */
    fun loadPage() {
        viewModelScope.launch {
            updateUiState {
                it.copy(isLoading = true)
            }
            val properties = propertyManager.getPropertyList(
                showAll = true,
            ).getOrElse {
                updateUiState {
                    it.copy(isLoading = false)
                }
                return@launch
            }
            val uiModels = properties.map {
                PropertyUIModel(it.id, it.name, it.address)
            }
            updateUiState {
                PropertyManagerUIState(
                    content = PropertyManagerUIModel(uiModels),
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Navigate to the property details screen.
     */
    fun navigateToPropertyDetails(propertyId: PropertyId) {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(
                    ManagementDestination.PropertyManagementDestination(
                        propertyId
                    )
                )
            )
        }
    }

    /**
     * Navigate to the add property screen.
     */
    fun navigateToAddProperty() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(ManagementDestination.AddPropertyManagementDestination)
            )
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateBack
            )
        }
    }

    companion object {
        const val TAG = "PropertyManagerViewModel"
    }
}
