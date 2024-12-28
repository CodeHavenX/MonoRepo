package com.cramsan.edifikana.client.lib.features.root.admin.properties

import com.cramsan.edifikana.client.lib.features.root.admin.AdminActivityEvent
import com.cramsan.edifikana.client.lib.features.root.admin.AdminRouteDestination
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the PropertyManager screen.
 **/
class PropertyManagerViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        PropertyManagerUIState(
            content = PropertyManagerUIModel(emptyList()),
            isLoading = false,
        )
    )

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<PropertyManagerUIState> = _uiState

    private val _event = MutableSharedFlow<PropertyManagerEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<PropertyManagerEvent> = _event

    /**
     * Load the page.
     */
    fun loadPage() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val properties = propertyManager.getPropertyList(
                showAll = true,
            ).getOrElse {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                return@launch
            }
            val uiModels = properties.map {
                PropertyUIModel(it.id, it.name, it.address)
            }
            _uiState.value = PropertyManagerUIState(
                content = PropertyManagerUIModel(uiModels),
                isLoading = false,
            )
        }
    }

    /**
     * Navigate to the property details screen.
     */
    fun navigateToPropertyDetails(propertyId: PropertyId) {
        viewModelScope.launch {
            _event.emit(
                PropertyManagerEvent.TriggerActivityEvent(
                    AdminActivityEvent.Navigate(
                        AdminRouteDestination.PropertyAdminDestination(
                            propertyId
                        )
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
            /*
            _event.emit(PropertyManagerEvent.TriggerActivityEvent(
                AdminActivityEvent.Navigate(propertyId)),
            )
             */
        }
    }

    companion object {
        private const val TAG = "PropertyManagerViewModel"
    }
}
