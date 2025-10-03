package com.cramsan.edifikana.client.lib.features.management.employee

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Employee feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class EmployeeUIState(
    val employeeId: EmployeeId?,
    val title: String?,
    val isLoading: Boolean,
    val idType: IdType?,
    val idNNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val role: EmployeeRole?,
    val isEditable: Boolean?,
    val canSave: Boolean?,
) : ViewModelUIState {
    companion object {
        val Initial = EmployeeUIState(
            employeeId = null,
            title = null,
            isLoading = true,
            idType = null,
            idNNumber = null,
            firstName = null,
            lastName = null,
            email = null,
            role = null,
            isEditable = null,
            canSave = null,
        )
    }
}
