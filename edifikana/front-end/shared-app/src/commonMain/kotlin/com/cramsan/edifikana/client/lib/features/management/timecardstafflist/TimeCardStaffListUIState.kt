package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Time Card Staff List screen.
 */
data class TimeCardStaffListUIState(
    val staffs: List<TimeCardStaffUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = TimeCardStaffListUIState(
            emptyList(),
            true,
            "",
        )
    }
}
