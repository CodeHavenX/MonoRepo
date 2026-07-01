package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.settings.getLastSelectedOrganizationId
import com.cramsan.edifikana.client.lib.settings.setLastSelectedOrganizationId
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the MyOrganizations screen.
 */
@FrontendViewModel
class MyOrganizationsViewModel(
    dependencies: ViewModelDependencies,
    private val organizationManager: OrganizationManager,
    private val membershipManager: MembershipManager,
    private val authManager: AuthManager,
    private val preferencesManager: PreferencesManager,
) : BaseViewModel<MyOrganizationsEvent, MyOrganizationsUIState>(
    dependencies,
    MyOrganizationsUIState.Initial,
    TAG,
) {
    /**
     * Loads the list of organizations the current user belongs to.
     */
    fun initialize() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }

            val currentUserId = authManager.activeUser().value
            val activeOrgId = preferencesManager.getLastSelectedOrganizationId()

            organizationManager
                .getOrganizations()
                .onSuccess { orgs ->
                    val items =
                        orgs.mapNotNull { org ->
                            val members = membershipManager.listMembers(org.id).getOrNull() ?: return@mapNotNull null
                            val myMembership =
                                members.firstOrNull { it.userId == currentUserId }
                                    ?: return@mapNotNull null
                            OrgListItemUIModel(
                                orgId = org.id,
                                name = org.name,
                                roleLabel = myMembership.role.toDisplayLabel(),
                                isActive = org.id == activeOrgId,
                            )
                        }
                    updateUiState { it.copy(isLoading = false, organizations = items) }
                }.onFailure {
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to load organizations"))
                }
        }
    }

    /**
     * Shows the switch organization confirmation dialog.
     */
    fun requestSwitchOrg(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = MyOrganizationsDialogState.ConfirmSwitchOrg(orgId)) }
        }
    }

    /**
     * Dismisses the active dialog without action.
     */
    fun dismissDialog() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = MyOrganizationsDialogState.None) }
        }
    }

    /**
     * Called when the user taps the active organization card. Navigates to its detail screen.
     */
    fun onOrgSelected(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    SettingsDestination.OrganizationDetailDestination(orgId),
                ),
            )
        }
    }

    /**
     * Switches the active organization and navigates to the Home graph.
     */
    fun onConfirmSwitchOrg(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            preferencesManager.setLastSelectedOrganizationId(orgId)
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearStack = true,
                ),
            )
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "MyOrganizationsViewModel"
    }
}

private fun OrgRole.toDisplayLabel(): String =
    when (this) {
        OrgRole.OWNER -> "Owner"
        OrgRole.ADMIN -> "Admin"
        OrgRole.MANAGER -> "Manager"
        OrgRole.EMPLOYEE -> "Employee"
    }
