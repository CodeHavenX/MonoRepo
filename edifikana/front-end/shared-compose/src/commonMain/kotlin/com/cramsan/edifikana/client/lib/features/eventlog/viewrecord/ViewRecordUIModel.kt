package com.cramsan.edifikana.client.lib.features.eventlog.viewrecord

import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString
import com.cramsan.edifikana.lib.EventLogRecordPK

/**
 * Represents the UI state of the View Record screen.
 */
data class ViewRecordUIModel(
    val summary: String,
    val description: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val attachments: List<AttachmentHolder>,
    val recordPK: EventLogRecordPK,
)

/**
 * Converts an [EventLogRecordModel] to a [ViewRecordUIModel].
 */
suspend fun EventLogRecordModel.toUIModel(): ViewRecordUIModel {
    return ViewRecordUIModel(
        summary = summary,
        description = description,
        eventType = eventType.toFriendlyString(),
        unit = unit,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        attachments = attachments,
        recordPK = requireNotNull(id),
    )
}
