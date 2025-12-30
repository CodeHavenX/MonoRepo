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

        val employeesResult = authManager.getUsers(orgId)
            .onFailure { throwable ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to load employees: ${throwable.message ?: "Unknown error"}"
                    )
                )
            }

        val invitesResult = authManager.getInvites(orgId)
            .onFailure { throwable ->
                emitWindowEvent(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Failed to load invites: ${throwable.message ?: "Unknown error"}"
                    )
                )
            }

        val inviteModels = invitesResult.getOrNull()?.map { invite ->
            InviteItemUIModel(
                email = invite.email,
            )
        }.orEmpty()

        val employeeModels = employeesResult.getOrNull()?.map { user ->
            UserItemUIModel(
                id = user.id,
                name = "${user.firstName} ${user.lastName}".trim(),
                email = user.email,
                imageUrl = null,
            )
        }.orEmpty()

        val combinedList = (employeeModels + inviteModels).sortedBy {
            when (it) {
                is UserItemUIModel -> it.name.lowercase()
                is InviteItemUIModel -> it.email.lowercase()
            }
        }

        updateUiState {
            it.copy(
                isLoading = false,
                employeeList = combinedList,

            )
        }
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
