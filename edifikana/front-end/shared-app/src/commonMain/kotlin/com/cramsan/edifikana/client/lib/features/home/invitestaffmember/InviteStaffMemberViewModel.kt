package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserRole
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.utils.loginvalidation.validateEmail
import kotlinx.coroutines.launch

/**
 * ViewModel for the InviteStaffMember screen.
 **/
class InviteStaffMemberViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
) : BaseViewModel<InviteStaffMemberEvent, InviteStaffMemberUIState>(
    dependencies,
    InviteStaffMemberUIState.Initial,
    TAG,
) {

    /**
     * Initialize the ViewModel with the organization ID.
     */
    fun initialize(orgId: OrganizationId) {
        viewModelScope.launch {
            val roles = listOf(
                StaffRoleUIModel(UserRole.ADMIN, "Admin"),
                StaffRoleUIModel(UserRole.MANAGER, "Manager"),
                StaffRoleUIModel(UserRole.EMPLOYEE, "Employee"),
            )
            updateUiState { it.copy(orgId = orgId, roles = roles) }
        }
    }

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Send an invitation to the specified email with the selected role.
     */
    fun sendInvitation(email: String, role: StaffRoleUIModel?) {
        viewModelScope.launch {
            val organizationId = requireNotNull(uiState.value.orgId)

            val emailErrors = validateEmail(email)
            if (emailErrors.isNotEmpty()) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(emailErrors.first())
                )
                return@launch
            }

            if (role == null) {
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar("Please select a role")
                )
                return@launch
            }

            updateUiState { it.copy(isLoading = true) }
            authManager.inviteEmployee(email, organizationId, role.role).onFailure {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to send invitation")
                )
                return@launch
            }

            emitWindowEvent(
                EdifikanaWindowsEvent.ShowSnackbar("Invitation sent to $email")
            )
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    companion object {
        private const val TAG = "InviteStaffMemberViewModel"
    }
}
