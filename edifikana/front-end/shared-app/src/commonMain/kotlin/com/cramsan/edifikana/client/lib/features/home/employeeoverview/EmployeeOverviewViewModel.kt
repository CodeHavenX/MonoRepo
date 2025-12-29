package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the EmployeeOverview screen.
 **/
class EmployeeOverviewViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
) : BaseViewModel<EmployeeOverviewEvent, EmployeeOverviewUIState>(
    dependencies,
    EmployeeOverviewUIState.Initial,
    TAG,
) {
    /**
     * Initialize the ViewModel.
     */
    fun initialize() {
        viewModelScope.launch {
            loadEmployeesAndInvites()
        }
    }

    /**
     * Set the organization ID and load employees and invites.
     */
    fun setOrgId(orgId: OrganizationId) {
        viewModelScope.launch {
            updateUiState {
                it.copy(orgId = orgId)
            }
            loadEmployeesAndInvites()
        }
    }

    private suspend fun loadEmployeesAndInvites() {
        val orgId = uiState.value.orgId ?: return

        updateUiState { it.copy(isLoading = true) }

        authManager.getUsers(orgId)
            .onSuccess { users ->
                val employeeModels = users.map { user ->
                    EmployeeItemUIModel(
                        id = user.id,
                        name = "${user.firstName} ${user.lastName}".trim().ifEmpty { user.email },
                        email = user.email,
                        imageUrl = null,
                    )
                }
                updateUiState { it.copy(employeeList = employeeModels) }
            }
            .onFailure { throwable ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to load employees: ${throwable.message ?: "Unknown error"}"
                    )
                )
            }

        authManager.getInvites(orgId)
            .onSuccess { invites ->
                val inviteModels = invites.map { invite ->
                    InviteItemUIModel(email = invite.email)
                }
                updateUiState { it.copy(inviteList = inviteModels) }
            }
            .onFailure { throwable ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to load invites: ${throwable.message ?: "Unknown error"}"
                    )
                )
            }

        updateUiState { it.copy(isLoading = false) }
    }

    /**
     * Navigate to the add employee screen.
     */
    fun navigateToAddEmployeeScreen() {
        viewModelScope.launch {
            uiState.value.orgId?.let { orgId ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.InviteStaffMemberDestination(
                            orgId = orgId,
                        )
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "EmployeeOverviewViewModel"
    }
}
