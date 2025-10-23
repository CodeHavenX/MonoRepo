package com.cramsan.edifikana.client.lib.features.management.hub

import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the Hub screen.
 **/
class HubViewModel(
    dependencies: ViewModelDependencies,
    private val organizationManager: OrganizationManager,
) : BaseViewModel<HubEvent, HubUIModel>(
    dependencies,
    HubUIModel.Empty,
    TAG,
) {
    init {
        viewModelScope.launch {
            organizationManager.observeActiveOrganization().collect { activeOrganization ->
                updateSelectedOrganization(activeOrganization)
            }
        }
    }

    private suspend fun updateSelectedOrganization(activeOrganization: Organization?) {
        updateUiState { currentState ->
            val updatedOrganizations = currentState.availableOrganizations.map { organizationUIModel ->
                organizationUIModel.copy(
                    selected = organizationUIModel.id == activeOrganization?.id
                )
            }
            currentState.copy(
                availableOrganizations = updatedOrganizations
            )
        }
    }

    /**
     * Initialize the view model.
     */
    fun loadInitialData() {
        logI(TAG, "Loading initial data for HubViewModel.")
        viewModelScope.launch {
            val organizations = organizationManager.getOrganizations().requireSuccess()
            val organizationsUIModels = organizations.map { it.toUIModel() }

            updateUiState {
                it.copy(
                    availableOrganizations = organizationsUIModels,
                )
            }

            val selectedOrganizations = organizationManager.getActiveOrganization().requireSuccess()
            updateSelectedOrganization(selectedOrganizations)
        }
    }

    /**
     * Select an organization by its ID.
     */
    fun selectOrganization(organizationId: OrganizationId) {
        logI(TAG, "Selecting organization with ID: ${organizationId.id}")
        viewModelScope.launch {
            organizationManager.setActiveOrganization(organizationId).requireSuccess()
        }
    }

    private fun Organization.toUIModel(): OrganizationUIModel {
        return OrganizationUIModel(
            id = this.id,
            name = this.id.id,
            selected = false,
        )
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        logI(TAG, "Navigating to account page.")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.AccountNavGraphDestination)
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
     * Navigate to the notifications screen.
     */
    fun navigateToNotifications() {
        logI(TAG, "Navigate to the notifications screen")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AccountDestination.NotificationsDestination,
                )
            )
        }
    }

    companion object {
        private const val TAG = "HubViewModel"
    }
}
