package com.cramsan.edifikana.client.lib.features.home.employeelist

import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the EmployeeList screen.
 **/
class EmployeeListViewModel(
    dependencies: ViewModelDependencies,
    private val employeeManager: EmployeeManager,
    private val authManager: AuthManager,
    private val organizationManager: OrganizationManager,
) : BaseViewModel<EmployeeListEvent, EmployeeListUIState>(
    dependencies,
    EmployeeListUIState.Initial,
    TAG,
) {

    init {
        viewModelScope.launch {
            organizationManager.observeActiveOrganization().collect { organization ->
                updateUiState { it.copy(activeOrgId = organization?.id) }
                loadEmployeeList()
            }
        }
    }

    /**
     * Load the employee list.
     */
    fun loadEmployeeList() {
        viewModelScope.launch {
            val orgId = uiState.value.activeOrgId
            if (orgId == null) {
                updateUiState {
                    it.copy(isLoading = false, employeeList = emptyList())
                }
            } else {
                val employeeList = employeeManager.getEmployeeList().requireSuccess()
                val userList = authManager.getUsers(orgId).requireSuccess()
                val inviteList = authManager.getInvites(orgId).requireSuccess()

                val employeeUIModel = employeeList.map { it.toUIModel() }
                val userUIModel = userList.map { it.toUIModel() }
                val inviteUIModel = inviteList.map { it.toUIModel() }

                val combinedList = (employeeUIModel + userUIModel + inviteUIModel).sortedBy {
                    when (it) {
                        is EmployeeMemberUIModel -> it.name
                        is UserUIModel -> it.name
                        is InviteUIModel -> it.email
                    }
                }

                updateUiState {
                    it.copy(isLoading = false, employeeList = combinedList)
                }
            }
        }
    }

    private fun UserModel.toUIModel(): EmployeeUIModel {
        return UserUIModel(
            userId = id,
            name = firstName,
            email = email,
        )
    }

    private fun Invite.toUIModel(): EmployeeUIModel {
        return InviteUIModel(
            inviteId = id,
            email = email,
        )
    }

    private fun EmployeeModel.toUIModel(): EmployeeUIModel {
        return EmployeeMemberUIModel(
            employeeId = id,
            name = firstName,
            email = email,
        )
    }

    /**
     * Navigate to the AddPrimaryEmployee screen.
     */
    fun navigateToAddPrimaryEmployee() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.AddPrimaryEmployeeManagementDestination)
            )
        }
    }

    /**
     * Navigate to the AddSecondaryEmployee screen.
     */
    fun navigateToAddSecondaryEmployee() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.AddSecondaryEmployeeManagementDestination)
            )
        }
    }

    /**
     * Navigate to the employee screen.
     */
    fun navigateToEmployee(employeeId: EmployeeId) {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.EmployeeDestination(employeeId))
            )
        }
    }

    companion object {
        private const val TAG = "EmployeeListViewModel"
    }
}
