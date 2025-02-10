package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Staff List screen.
 */
data class StaffListUIState(
    val staffs: List<StaffUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = StaffListUIState(
            emptyList(),
            true,
            "",
        )
    }
}
