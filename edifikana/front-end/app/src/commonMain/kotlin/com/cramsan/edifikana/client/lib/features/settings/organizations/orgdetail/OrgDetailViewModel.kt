package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.settings.getLastSelectedOrganizationId
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * ViewModel for the OrgDetail screen.
 */
@FrontendViewModel
class OrgDetailViewModel(
    dependencies: ViewModelDependencies,
    private val organizationManager: OrganizationManager,
    private val membershipManager: MembershipManager,
    private val authManager: AuthManager,
    private val preferencesManager: PreferencesManager,
    private val propertyManager: PropertyManager,
) : BaseViewModel<OrgDetailEvent, OrgDetailUIState>(
    dependencies,
    OrgDetailUIState.Initial,
    TAG,
) {
    /**
     * Loads organization details and the current user's membership info.
     */
    fun initialize(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }

            val currentUserId = authManager.activeUser().value
            val activeOrgId = preferencesManager.getLastSelectedOrganizationId()

            val org = organizationManager.getOrganization(orgId).getOrNull()
            val members = membershipManager.listMembers(orgId).getOrNull()
            val propertyCount =
                propertyManager
                    .getPropertyList()
                    .getOrNull()
                    ?.count { it.organizationId == orgId }
                    ?: 0

            if (org == null || members == null) {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to load organization details"))
                return@launch
            }

            val myMembership = members.firstOrNull { it.userId == currentUserId }
            val ownerCount = members.count { it.role == OrgRole.OWNER }
            val isSoleOwner = myMembership?.role == OrgRole.OWNER && ownerCount == 1

            updateUiState {
                it.copy(
                    isLoading = false,
                    orgName = org.name,
                    isActiveOrg = org.id == activeOrgId,
                    userRole = myMembership?.role,
                    memberCount = members.size,
                    propertyCount = propertyCount,
                    joinedDate = myMembership?.joinedAt?.toDisplayDate() ?: "",
                    isSoleOwner = isSoleOwner,
                )
            }
        }
    }

    /**
     * Shows the leave organization confirmation dialog.
     */
    fun onLeaveOrganizationTapped() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = OrgDetailDialogState.ConfirmLeave) }
        }
    }

    /**
     * Dismisses the active dialog without action.
     */
    fun dismissDialog() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = OrgDetailDialogState.None) }
        }
    }

    /**
     * Confirms leaving the organization, then navigates back to MyOrganizations.
     */
    fun confirmLeaveOrganization(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true, dialog = OrgDetailDialogState.None) }
            membershipManager
                .leaveOrganization(orgId)
                .onSuccess {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.NavigateToScreen(
                            SettingsDestination.MyOrganizationsDestination,
                            clearStack = true,
                        ),
                    )
                }.onFailure {
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to leave organization"))
                }
        }
    }

    /**
     * Navigates to the TransferOwnership screen for [orgId].
     */
    fun onTransferOwnershipTapped(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    SettingsDestination.TransferOwnershipDestination(orgId),
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
        private const val TAG = "OrgDetailViewModel"
    }
}

private fun Instant.toDisplayDate(): String {
    val local = toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.month.number}/${local.day}/${local.year}"
}
