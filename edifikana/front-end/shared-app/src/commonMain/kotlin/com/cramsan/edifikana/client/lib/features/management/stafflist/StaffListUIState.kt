package com.cramsan.edifikana.client.lib.features.management.stafflist

import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the StaffList feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class StaffListUIState(
    val isLoading: Boolean,
    val staffList: List<StaffUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = StaffListUIState(true, emptyList())
    }
}

/**
 * UI model for a staff member.
 *
 * This class models the data that is displayed in the staff list.
 */
data class StaffUIModel(
    val id: StaffId,
    val name: String,
    val email: String?,
    val status: StaffStatus,
)
