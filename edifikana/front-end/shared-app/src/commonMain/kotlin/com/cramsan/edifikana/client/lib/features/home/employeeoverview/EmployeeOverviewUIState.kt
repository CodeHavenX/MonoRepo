package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the EmployeeOverview feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class EmployeeOverviewUIState(
    val isLoading: Boolean,
    val orgId: OrganizationId?,
    val employeeList: List<EmployeeItemUIModel>,
    val inviteList: List<InviteItemUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = EmployeeOverviewUIState(
            isLoading = true,
            orgId = null,
            employeeList = emptyList(),
            inviteList = emptyList(),
        )
    }
}

/**
 * UI model to represent an employee in the employees list.
 */
data class EmployeeItemUIModel(
    val id: UserId,
    val name: String,
    val email: String,
    val imageUrl: String?,
)

/**
 * UI model to represent a pending invite in the invites list.
 */
data class InviteItemUIModel(
    val email: String,
)
