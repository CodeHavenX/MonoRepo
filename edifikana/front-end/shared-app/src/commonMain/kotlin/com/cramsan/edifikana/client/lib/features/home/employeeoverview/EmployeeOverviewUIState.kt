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
) : ViewModelUIState {
    companion object {
        val Initial = EmployeeOverviewUIState(
            isLoading = true,
            orgId = null,
            employeeList = emptyList(),
        )
    }
}

/**
 * UI model to represent an employee or an invitation in the employees list.
 */
sealed interface EmployeeItemUIModel {
    val email: String
}

/**
 * UI model to represent a user employee in the employees list.
 */
data class UserItemUIModel(val id: UserId, val name: String, override val email: String, val imageUrl: String?) :
    EmployeeItemUIModel

/**
 * UI model to represent an invitation in the employees list.
 */
data class InviteItemUIModel(override val email: String) : EmployeeItemUIModel
