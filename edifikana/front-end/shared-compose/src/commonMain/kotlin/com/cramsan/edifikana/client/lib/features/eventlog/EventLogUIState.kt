package com.cramsan.edifikana.client.lib.features.eventlog

data class EventLogUIState(
    val records: List<EventLogRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
)
