package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the EmployeeOverview feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class EmployeeOverviewUIState(
    val isLoading: Boolean,
    val employeeList: List<EmployeeItemUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = EmployeeOverviewUIState(
            isLoading = true,
            employeeList = emptyList(),
        )
    }
}

/**
 * UI model to represent an employee in the employees list.
 */
data class EmployeeItemUIModel(
    val id: EmployeeId,
    val name: String,
    val role: String,
    val imageUrl: String?,
)
