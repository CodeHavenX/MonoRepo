package com.cramsan.edifikana.client.android.features.eventlog

import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

data class EventLogRecordUIModel(
    val summary: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val recordPK: EventLogRecordPK,
)

suspend fun EventLogRecordModel.toUIModel(): EventLogRecordUIModel {
    return EventLogRecordUIModel(
        summary = summary,
        eventType = eventType.toFriendlyString(),
        unit = unit.orEmpty(),
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        recordPK = id,
    )
}