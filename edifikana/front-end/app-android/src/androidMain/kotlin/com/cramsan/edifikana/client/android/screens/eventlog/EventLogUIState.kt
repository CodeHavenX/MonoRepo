package com.cramsan.edifikana.client.android.screens.eventlog

import com.cramsan.edifikana.client.android.screens.eventlog.single.toFriendlyString
import com.cramsan.edifikana.client.android.utils.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

sealed class EventLogUIState {

    data object Loading : EventLogUIState()

    data object Empty : EventLogUIState()

    data class Success(val records: List<EventLogRecordUIModel>) : EventLogUIState()

    data class Error(val messageRes: Int) : EventLogUIState()
}

data class EventLogRecordUIModel (
    val summary: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val recordPK: EventLogRecordPK,
)

fun EventLogRecord.toUIModel(): EventLogRecordUIModel {
    return EventLogRecordUIModel(
        summary = summary.orEmpty(),
        eventType = eventType.toFriendlyString(),
        unit = unit.orEmpty(),
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        recordPK = documentId(),
    )
}