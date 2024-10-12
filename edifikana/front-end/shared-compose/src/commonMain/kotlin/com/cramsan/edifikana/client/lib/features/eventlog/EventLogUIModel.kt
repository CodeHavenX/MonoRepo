package com.cramsan.edifikana.client.lib.features.eventlog

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString

/**
 * Represents the UI state of the View Record screen.
 */
data class EventLogRecordUIModel(
    val summary: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val recordPK: EventLogRecordPK?,
    val clickable: Boolean,
)

/**
 * Converts an [EventLogRecordModel] to a [EventLogRecordUIModel].
 */
suspend fun EventLogRecordModel.toUIModel(): EventLogRecordUIModel {
    return EventLogRecordUIModel(
        summary = summary,
        eventType = eventType.toFriendlyString(),
        unit = unit,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        recordPK = id,
        clickable = id != null
    )
}
