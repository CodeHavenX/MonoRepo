package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the PropertiesOverview screen.
 **/
class PropertiesOverviewViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
    private val organizationManager: OrganizationManager,
) : BaseViewModel<PropertiesOverviewEvent, PropertiesOverviewUIState>(
    dependencies,
    PropertiesOverviewUIState.Initial,
    TAG,
) {
    /**
     * Initialize the ViewModel.
     */
    fun initialize() {
        viewModelScope.launch {
            updateUiState {
                it.copy(isLoading = true)
            }
            propertyManager.getPropertyList()
                .onSuccess { resultList ->
                    val uiModels = resultList.map { property ->
                        PropertyItemUIModel.fromDomainModel(property)
                    }
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            propertyList = uiModels,
                        )
                    }
                }
                .onFailure { throwable ->
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.ShowSnackbar(
                            "Failed to load properties: ${throwable.message ?: "Unknown error"}"
                        )
                    )
                }
        }
    }

    /**
     * Called when the user selects to add a new property.
     */
    fun onAddPropertySelected() {
        viewModelScope.launch {
            val organization = organizationManager.getOrganizations().getOrThrow().firstOrNull()

            if (organization == null) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "No organization found. Please create an organization first."
                    )
                )
                return@launch
            }

            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    HomeDestination.AddPropertyManagementDestination(
                        orgId = organization.id,
                    )
                )
            )
        }
    }

    /**
     * Called when the user selects a property from the list.
     */
    fun onPropertySelected(property: PropertyItemUIModel) {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    HomeDestination.PropertyManagementDestination(
                        propertyId = property.id,
                    )
                )
            )
        }
    }

    companion object {
        private const val TAG = "PropertiesOverviewViewModel"
    }
}
