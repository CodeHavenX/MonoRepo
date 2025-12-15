package com.cramsan.edifikana.client.lib.features.home.eventlog

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the EventLog feature.
 *
 * This class models the top level state of the page.
 */
data class EventLogUIState(
    val isLoading: Boolean,
    val events: List<EventLogUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = EventLogUIState(
            isLoading = true,
            events = emptyList(),
        )
    }
}

/**
 * UI model for a single event log entry.
 */
data class EventLogUIModel(
    val id: EventLogEntryId?,
    val title: String,
    val description: String,
    val eventType: EventLogEventType,
    val fallbackEventType: String?,
    val unit: String,
    val timeRecorded: String,
    val employeeName: String?,
)
