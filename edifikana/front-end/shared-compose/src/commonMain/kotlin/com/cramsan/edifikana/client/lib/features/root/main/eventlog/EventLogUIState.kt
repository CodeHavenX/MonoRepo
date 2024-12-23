package com.cramsan.edifikana.client.lib.features.root.main.eventlog

/**
 * Represents the UI state of the Event Log screen.
 */
data class EventLogUIState(
    val records: List<EventLogRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
)
