package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.EventLogEventType
import kotlin.random.Random

data class EventLogRecordModel(
    val id: String?,
    val entityId: String?,
    val employeePk: String?,
    val timeRecorded: Long,
    val unit: String,
    val eventType: EventLogEventType,
    val fallbackEmployeeName: String?,
    val fallbackEventType: String?,
    val summary: String,
    val description: String,
    val attachments: List<AttachmentHolder>,
) {
    companion object {

        /**
         * Create a temporary [EventLogRecordModel] that is intended to represent a record that has not been uploaded
         * yet.
         */
        fun createTemporary(
            employeePk: String?,
            timeRecorded: Long,
            unit: String,
            eventType: EventLogEventType?,
            fallbackEmployeeName: String?,
            fallbackEventType: String?,
            summary: String,
            description: String,
        ): EventLogRecordModel {
            // TODO: Use a better entity Id
            return EventLogRecordModel(
                id = null,
                entityId = Random.nextInt().toString(),
                employeePk = employeePk,
                timeRecorded = timeRecorded,
                unit = unit.trim(),
                eventType = eventType ?: EventLogEventType.INCIDENT,
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
