package com.cramsan.edifikana.client.android.features.eventlog.viewrecord

import com.cramsan.edifikana.client.android.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toFriendlyString
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

data class ViewRecordUIModel(
    val summary: String,
    val description: String,
    val eventType: String,
    val unit: String,
    val timeRecorded: String,
    val publicAttachmentUris: List<String>,
    val recordPK: EventLogRecordPK,
)

suspend fun EventLogRecordModel.toUIModel(): ViewRecordUIModel {
    return ViewRecordUIModel(
        summary = summary,
        description = description,
        eventType = eventType.toFriendlyString(),
        unit = unit,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        publicAttachmentUris = attachments,
        recordPK = id,
    )
}