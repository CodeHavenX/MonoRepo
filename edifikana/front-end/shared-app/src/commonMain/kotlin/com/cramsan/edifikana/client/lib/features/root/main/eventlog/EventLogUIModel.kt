package com.cramsan.edifikana.client.lib.features.root.main.eventlog

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString
import com.cramsan.edifikana.lib.model.EventLogEntryId

/**
 * Represents the UI state of the View Record screen.
 */
data class EventLogRecordUIModel(
    val title: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val recordPK: EventLogEntryId?,
    val clickable: Boolean,
)

/**
 * Converts an [EventLogRecordModel] to a [EventLogRecordUIModel].
 */
suspend fun EventLogRecordModel.toUIModel(): EventLogRecordUIModel {
    return EventLogRecordUIModel(
        title = title,
        eventType = eventType.toFriendlyString(),
        unit = unit,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        recordPK = id,
        clickable = id != null
    )
}
