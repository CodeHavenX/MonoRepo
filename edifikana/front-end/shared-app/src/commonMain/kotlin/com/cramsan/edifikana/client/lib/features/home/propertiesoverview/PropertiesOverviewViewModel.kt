package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

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
            val resultList = propertyManager.getPropertyList()
                .getOrThrow()
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
    }

    /**
     * Called when the user selects to add a new property.
     */
    fun onAddPropertySelected() = Unit

    /**
     * Called when the user selects a property from the list.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onPropertySelected(property: PropertyItemUIModel) = Unit

    companion object {
        private const val TAG = "PropertiesOverviewViewModel"
    }
}
