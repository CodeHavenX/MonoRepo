package com.cramsan.edifikana.client.android.models

import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType

data class EventLogRecordModel(
    val id: EventLogRecordPK,
    val employeePk: EmployeePK?,
    val timeRecorded: Long,
    val unit: String,
    val eventType: EventType,
    val fallbackEmployeeName: String?,
    val fallbackEventType: String?,
    val summary: String,
    val description: String,
    val attachments: List<AttachmentHolder>,
)

data class AttachmentHolder(
    val publicUrl: String,
    val storageRef: StorageRef?,
)
