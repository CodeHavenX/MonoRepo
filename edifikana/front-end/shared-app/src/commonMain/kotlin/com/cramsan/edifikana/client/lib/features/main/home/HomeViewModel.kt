package com.cramsan.edifikana.client.lib.features.main.home

import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 **/
class HomeViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel<HomeEvent, HomeUIModel>(
    dependencies,
    HomeUIModel.Empty,
    TAG,
) {

    /**
     * Load properties.
     */
    fun loadContent() {
        logI(TAG, "Loading properties.")
        viewModelScope.launch {
            updatePropertyList()
        }
    }

    /**
     * Select property.
     */
    fun selectProperty(propertyId: PropertyId) {
        logI(TAG, "Property selected: $propertyId")
        viewModelScope.launch {
            propertyManager.setActiveProperty(propertyId)
            updatePropertyList()
        }
    }

    private suspend fun updatePropertyList() {
        val properties = propertyManager.getPropertyList().getOrThrow()
        val selectedProperty = propertyManager.activeProperty().value
        var name = ""
        updateUiState {
            val propertyUiModels = properties.map { property ->
                val isSelected = property.id == selectedProperty
                if (isSelected) {
                    name = property.name
                }
                property.toUIModel(selected = isSelected)
            }
            it.copy(
                label = name,
                availableProperties = propertyUiModels,
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            logI(TAG, "Navigating back.")
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToActivity(
                    ActivityRouteDestination.AccountRouteDestination,
                )
            )
        }
    }

    /**
     * Set the selected tab.
     */
    fun selectTab(selectedTab: Tabs) {
        viewModelScope.launch {
            updateUiState { it.copy(selectedTab = selectedTab) }
        }
    }

    /**
     * Navigate to the notifications page.
     */
    fun navigateToNotifications() {
        logI(TAG, "Navigating to notifications page.")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(AccountRouteDestination.NotificationsDestination)
            )
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
