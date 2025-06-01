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
    val propertyName: String?,
    val address: String?,
    val isLoading: Boolean,
    val managers: List<String>,
    val addManagerError: Boolean,
    val addManagerEmail: String,
    val suggestions: List<String>,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyUIState(
            propertyName = null,
            address = null,
            isLoading = false,
            managers = emptyList(),
            addManagerError = false,
            addManagerEmail = "",
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
    val email: String,
    val staffId: StaffId,
)
