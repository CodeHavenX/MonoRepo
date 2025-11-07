package com.cramsan.edifikana.client.lib.features.home.employeelist

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the EmployeeList feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class EmployeeListUIState(
    val isLoading: Boolean,
    val employeeList: List<EmployeeUIModel>,
    val activeOrgId: OrganizationId?,
) : ViewModelUIState {
    companion object {
        val Initial = EmployeeListUIState(true, emptyList(), null)
    }
}

/**
 * Sealed interface representing a employee member in the UI.
 *
 * This can be either a user, an invite, or a employee member.
 */
sealed interface EmployeeUIModel

/**
 * UI model for a employee member.
 *
 * This class models the data that is displayed in the employee list.
 */
data class UserUIModel(
    val userId: UserId,
    val name: String,
    val email: String?,
) : EmployeeUIModel

/**
 * UI model for an invite.
 *
 * This class models the data that is displayed in the employee list for an invite.
 */
data class InviteUIModel(
    val inviteId: InviteId,
    val email: String,
) : EmployeeUIModel

/**
 * UI model for a employee member.
 *
 * This class models the data that is displayed in the employee list for a employee member.
 */
data class EmployeeMemberUIModel(
    val employeeId: EmployeeId,
    val name: String,
    val email: String?,
) : EmployeeUIModel
