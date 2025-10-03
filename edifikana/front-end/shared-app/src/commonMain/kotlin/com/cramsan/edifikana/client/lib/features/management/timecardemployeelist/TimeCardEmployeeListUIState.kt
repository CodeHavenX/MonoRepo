package com.cramsan.edifikana.client.lib.features.management.timecardemployeelist

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Time Card Employee List screen.
 */
data class TimeCardEmployeeListUIState(
    val employees: List<TimeCardEmployeeUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = TimeCardEmployeeListUIState(
            emptyList(),
            true,
            "",
        )
    }
}
