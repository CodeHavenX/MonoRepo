package com.cramsan.edifikana.client.lib.features.management.viewrecord

import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.framework.core.compose.resources.StringProvider

/**
 * Represents the UI state of the View Record screen.
 */
data class ViewRecordUIModel(
    val title: String,
    val description: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val attachments: List<AttachmentHolder>,
    val recordPK: EventLogEntryId,
)

/**
 * Converts an [EventLogRecordModel] to a [ViewRecordUIModel].
 */
suspend fun EventLogRecordModel.toUIModel(
    stringProvider: StringProvider,
): ViewRecordUIModel {
    return ViewRecordUIModel(
        title = title,
        description = description,
        eventType = eventType.toFriendlyString(stringProvider),
        unit = unit,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        attachments = attachments,
        recordPK = requireNotNull(id),
    )
}
