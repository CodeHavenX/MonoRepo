package com.cramsan.edifikana.client.lib.features.management.property

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Property feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyUIState(
    val title: String?,
    val propertyName: String?,
    val address: String?,
    val isLoading: Boolean,
    val employee: List<EmployeeUIModel>,
    val addEmployeeError: Boolean,
    val addEmployeeEmail: String,
    val suggestions: List<String>,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyUIState(
            title = null,
            propertyName = null,
            address = null,
            isLoading = false,
            employee = emptyList(),
            addEmployeeError = false,
            addEmployeeEmail = "",
            suggestions = emptyList(),
        )
    }
}

/**
 * UI model for an employee member.
 *
 * This class models the data that is displayed in the employee list.
 */
data class EmployeeUIModel(
    val employeeId: EmployeeId?,
    val email: String,
    val isRemoving: Boolean,
)
