package com.cramsan.edifikana.client.lib.features.management.property

import com.cramsan.edifikana.lib.model.StaffId
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
    val staff: List<StaffUIModel>,
    val addStaffError: Boolean,
    val addStaffEmail: String,
    val suggestions: List<String>,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyUIState(
            title = null,
            propertyName = null,
            address = null,
            isLoading = false,
            staff = emptyList(),
            addStaffError = false,
            addStaffEmail = "",
            suggestions = emptyList(),
        )
    }
}

/**
 * UI model for a staff member.
 *
 * This class models the data that is displayed in the staff list.
 */
data class StaffUIModel(
    val staffId: StaffId?,
    val email: String,
    val isRemoving: Boolean,
)
