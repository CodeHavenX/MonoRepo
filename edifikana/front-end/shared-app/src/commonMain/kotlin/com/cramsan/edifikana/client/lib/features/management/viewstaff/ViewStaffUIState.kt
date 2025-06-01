package com.cramsan.edifikana.client.lib.features.management.viewstaff

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the View Staff screen.
 */
data class ViewStaffUIState(
    val isLoading: Boolean,
    val staff: ViewStaffUIModel.StaffUIModel?,
    val records: List<ViewStaffUIModel.TimeCardRecordUIModel>,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = ViewStaffUIState(
            true,
            null,
            emptyList(),
            "",
        )
    }
}
