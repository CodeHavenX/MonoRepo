package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the TransferOwnership screen.
 */
@FrontendViewModel
class TransferOwnershipViewModel(
    dependencies: ViewModelDependencies,
    private val membershipManager: MembershipManager,
    private val authManager: AuthManager,
) : BaseViewModel<TransferOwnershipEvent, TransferOwnershipUIState>(
    dependencies,
    TransferOwnershipUIState.Initial,
    TAG,
) {
    /**
     * Loads the list of admin-role members eligible for ownership transfer.
     */
    fun initialize(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }

            val currentUserId = authManager.activeUser().value
            membershipManager
                .listMembers(orgId)
                .onSuccess { members ->
                    val admins =
                        members
                            .filter { it.role == OrgRole.ADMIN && it.userId != currentUserId }
                            .map { AdminUIModel(userId = it.userId, displayName = it.displayName, email = it.email) }
                    updateUiState { it.copy(isLoading = false, eligibleAdmins = admins) }
                }.onFailure {
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to load members"))
                }
        }
    }

    /**
     * Sets the selected admin and shows the confirmation dialog.
     */
    fun onAdminSelected(admin: AdminUIModel) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = TransferOwnershipDialogState.ConfirmTransfer(admin)) }
        }
    }

    /**
     * Dismisses the active dialog without action.
     */
    fun dismissDialog() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(dialog = TransferOwnershipDialogState.None) }
        }
    }

    /**
     * Confirms the ownership transfer and navigates back on success.
     */
    fun confirmTransferOwnership(orgId: OrganizationId) {
        viewModelCoroutineScope.launch {
            val target =
                (uiState.value.dialog as? TransferOwnershipDialogState.ConfirmTransfer)?.target ?: return@launch
            updateUiState { it.copy(isLoading = true, dialog = TransferOwnershipDialogState.None) }
            membershipManager
                .transferOwnership(orgId, target.userId)
                .onSuccess {
                    emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
                }.onFailure {
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Failed to transfer ownership"))
                }
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
        private const val TAG = "TransferOwnershipViewModel"
    }
}
