package com.cramsan.edifikana.client.lib.features.home.propertyhome

import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 **/
class PropertyHomeViewModel(
    dependencies: ViewModelDependencies,
    private val propertyManager: PropertyManager,
) : BaseViewModel<PropertyHomeEvent, PropertyHomeUIModel>(
    dependencies,
    PropertyHomeUIModel.Empty,
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
        val properties = propertyManager.getPropertyList().getOrNull().orEmpty()
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
        // Check if there is a list of properties. If the list is empty, show the fallback tab.
        if (properties.isEmpty()) {
            logI(TAG, "No properties found. Show the fallback tab.")
            selectTab(Tabs.GoToOrganization)
        } else {
            logD(TAG, "Properties found. Show the properties tab.")
            selectTab(Tabs.EventLog)
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
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AccountNavGraphDestination,
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
                EdifikanaWindowsEvent.NavigateToScreen(AccountDestination.NotificationsDestination)
            )
        }
    }

    fun navigateToSettings() {
        logI(TAG, "Navigating to settings page.")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.SettingsNavGraphDestination,

            ))
        }
    }

    companion object {
        private const val TAG = "PropertyHomeViewModel"
    }
}
