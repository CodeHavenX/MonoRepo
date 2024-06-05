package com.cramsan.edifikana.client.android.models

import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import kotlin.random.Random

data class EventLogRecordModel(
    val id: EventLogRecordPK?,
    val entityId: String?,
    val employeePk: EmployeePK?,
    val timeRecorded: Long,
    val unit: String,
    val eventType: EventType,
    val fallbackEmployeeName: String?,
    val fallbackEventType: String?,
    val summary: String,
    val description: String,
    val attachments: List<AttachmentHolder>,
) {
    companion object {
        fun createTemporary(
            employeePk: EmployeePK?,
            timeRecorded: Long,
            unit: String,
            eventType: EventType?,
            fallbackEmployeeName: String?,
            fallbackEventType: String?,
            summary: String,
            description: String,
        ) : EventLogRecordModel {
            // TODO: Use a better entity Id
            return EventLogRecordModel(
                id = null,
                entityId = Random.nextInt().toString(),
                employeePk = employeePk,
                timeRecorded = timeRecorded,
                unit = unit.trim(),
                eventType = eventType ?: EventType.INCIDENT,
                fallbackEmployeeName = fallbackEmployeeName?.trim(),
                fallbackEventType = fallbackEventType?.trim(),
                summary = summary.trim(),
                description = description.trim(),
                attachments = emptyList(),
            )
        }
    }
}

data class AttachmentHolder(
    val publicUrl: String,
    val storageRef: StorageRef?,
)
