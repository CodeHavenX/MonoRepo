package com.cramsan.edifikana.client.lib.features.main.eventlog

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state of the Event Log screen.
 */
data class EventLogUIState(
    val records: List<EventLogRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
) : ViewModelUIState {
    companion object {
        val Empty = EventLogUIState(emptyList(), true, "")
    }
}
