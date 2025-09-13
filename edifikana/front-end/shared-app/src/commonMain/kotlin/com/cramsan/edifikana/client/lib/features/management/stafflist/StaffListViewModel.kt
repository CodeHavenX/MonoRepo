package com.cramsan.edifikana.client.lib.features.management.stafflist

import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the StaffList screen.
 **/
class StaffListViewModel(
    dependencies: ViewModelDependencies,
    private val staffManager: StaffManager,
    private val authManager: AuthManager,
    private val organizationManager: OrganizationManager,
) : BaseViewModel<StaffListEvent, StaffListUIState>(
    dependencies,
    StaffListUIState.Initial,
    TAG,
) {

    init {
        viewModelScope.launch {
            organizationManager.observeActiveOrganization().collect { organization ->
                updateUiState { it.copy(activeOrgId = organization?.id) }
                loadStaffList()
            }
        }
    }

    /**
     * Load the staff list.
     */
    fun loadStaffList() {
        viewModelScope.launch {
            val orgId = uiState.value.activeOrgId
            if (orgId == null) {
                updateUiState {
                    it.copy(isLoading = false, staffList = emptyList())
                }
            } else {
                val staffList = staffManager.getStaffList().requireSuccess()
                val userList = authManager.getUsers(orgId).requireSuccess()
                val inviteList = authManager.getInvites(orgId).requireSuccess()

                val staffUIModel = staffList.map { it.toUIModel() }
                val userUIModel = userList.map { it.toUIModel() }
                val inviteUIModel = inviteList.map { it.toUIModel() }

                val combinedList = (staffUIModel + userUIModel + inviteUIModel).sortedBy {
                    when (it) {
                        is StaffMemberUIModel -> it.name
                        is UserUIModel -> it.name
                        is InviteUIModel -> it.email
                    }
                }

                updateUiState {
                    it.copy(isLoading = false, staffList = combinedList)
                }
            }
        }
    }

    private fun UserModel.toUIModel(): StaffUIModel {
        return UserUIModel(
            userId = id,
            name = firstName,
            email = email,
        )
    }

    private fun Invite.toUIModel(): StaffUIModel {
        return InviteUIModel(
            inviteId = id,
            email = email,
        )
    }

    private fun StaffModel.toUIModel(): StaffUIModel {
        return StaffMemberUIModel(
            staffId = id,
            name = firstName,
            email = email,
        )
    }

    /**
     * Navigate to the AddPrimaryStaff screen.
     */
    fun navigateToAddPrimaryStaff() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddPrimaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the AddSecondaryStaff screen.
     */
    fun navigateToAddSecondaryStaff() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.AddSecondaryStaffManagementDestination)
            )
        }
    }

    /**
     * Navigate to the staff screen.
     */
    fun navigateToStaff(staffId: StaffId) {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(ManagementDestination.StaffDestination(staffId))
            )
        }
    }

    companion object {
        private const val TAG = "StaffListViewModel"
    }
}
