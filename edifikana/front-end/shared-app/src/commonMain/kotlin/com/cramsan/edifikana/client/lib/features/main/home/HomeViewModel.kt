package com.cramsan.edifikana.client.lib.features.main.home

import com.cramsan.edifikana.client.lib.features.ActivityDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
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
    private val authManager: AuthManager,
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
            loadUserInformation()
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

    private suspend fun loadUserInformation() {
        val user = authManager.getUser(checkGlobalPerms = true).getOrThrow()
        updateUiState {
            it.copy(
                showAdminButton = user.hasGlobalPerms,
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            logI(TAG, "Navigating back.")
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateBack()
            )
        }
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityDestination.AccountDestination,
                )
            )
        }
    }

    /**
     * Navigate to the admin page.
     */
    fun navigateToAdmin() {
        logI(TAG, "Navigating to admin page.")
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.AdminDestination)
            )
        }
    }

    private suspend fun emitApplicationEvent(applicationEvent: EdifikanaApplicationEvent) {
        emitEvent(
            HomeEvent.TriggerApplicationEvent(applicationEvent)
        )
    }

    /**
     * Set the selected tab.
     */
    fun selectTab(selectedTab: Tabs) {
        updateUiState { it.copy(selectedTab = selectedTab) }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
