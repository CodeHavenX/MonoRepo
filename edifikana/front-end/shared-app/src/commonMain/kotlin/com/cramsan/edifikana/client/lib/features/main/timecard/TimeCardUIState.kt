package com.cramsan.edifikana.client.lib.features.main.timecard

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Time Card screen.
 */
data class TimeCardUIState(
    val timeCardEvents: List<TimeCardUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = TimeCardUIState(emptyList(), true, "")
    }
}
