package com.cramsan.edifikana.client.lib.features.root.admin.property

import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(
        PropertyUIState(
            content = null,
            isLoading = true,
        )
    )

    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<PropertyUIState> = _uiState

    private val _event = MutableSharedFlow<PropertyEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<PropertyEvent> = _event

    fun loadProperty(propertyId: PropertyId) {
        viewModelScope.launch {
            propertyManager.getProperty(propertyId).getOrElse {
                return@launch
            }.let {
                _uiState.value = PropertyUIState(
                    content = PropertyUIModel(it.id, it.name),
                    isLoading = false,
                )
            }
        }
    }

    companion object {
        private const val TAG = "PropertyViewModel"
    }
}
