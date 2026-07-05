package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import kotlinx.coroutines.launch

/**
 * ViewModel for the InviteStaffMember screen.
 **/
@FrontendViewModel
class InviteStaffMemberViewModel(dependencies: ViewModelDependencies, private val authManager: AuthManager) :
    BaseViewModel<InviteStaffMemberEvent, InviteStaffMemberUIState>(
        dependencies,
        InviteStaffMemberUIState.Initial,
        TAG,
    ) {
    private var orgId: OrganizationId? = null

    /**
     * Initialize the ViewModel with the organization ID.
     */
    fun initialize(orgId: OrganizationId) {
        this.orgId = orgId
        viewModelCoroutineScope.launch {
            val roles =
                listOf(
                    StaffRoleUIModel(InviteRole.ADMIN, "Admin"),
                    StaffRoleUIModel(InviteRole.MANAGER, "Manager"),
                    StaffRoleUIModel(InviteRole.EMPLOYEE, "Employee"),
                )
            updateUiState { it.copy(roles = roles) }
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Send an invitation to the specified email with the selected role.
     */
    fun sendInvitation(email: String, role: StaffRoleUIModel?) {
        viewModelCoroutineScope.launch {
            val organizationId = requireNotNull(orgId)

            val emailErrors = validateEmail(email)
            if (emailErrors.isNotEmpty()) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(emailErrors.first()),
                )
                return@launch
            }

            if (role == null) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar("Please select a role"),
                )
                return@launch
            }

            updateUiState { it.copy(isLoading = true) }
            authManager.inviteEmployee(email, organizationId, role.role).onFailure {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to send invitation"),
                )
                return@launch
            }

            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar("Invitation sent to $email"),
            )
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack,
            )
        }
    }

    companion object {
        private const val TAG = "InviteStaffMemberViewModel"
    }
}
