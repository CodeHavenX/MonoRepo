package com.cramsan.edifikana.client.android.features.eventlog

data class EventLogUIState(
    val records: List<EventLogRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
)
