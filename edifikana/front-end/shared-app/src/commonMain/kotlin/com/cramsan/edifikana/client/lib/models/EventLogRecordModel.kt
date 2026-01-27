package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Model for an event log record.
 */
data class EventLogRecordModel(
    val id: EventLogEntryId?,
    val entityId: String?,
    val employeePk: EmployeeId?,
    val propertyId: PropertyId,
    val timeRecorded: Long,
    val unit: String,
    val eventType: EventLogEventType,
    val fallbackEmployeeName: String?,
    val fallbackEventType: String?,
    val title: String,
    val description: String,
    val attachments: List<AttachmentHolder>,
) {
    companion object {

        /**
         * Create a temporary [EventLogRecordModel] that is intended to represent a record that has not been uploaded
         * yet.
         */
        fun createTemporary(
            employeePk: EmployeeId?,
            timeRecorded: Long,
            propertyId: PropertyId,
            unit: String,
            eventType: EventLogEventType?,
            fallbackEmployeeName: String?,
            fallbackEventType: String?,
            title: String,
            description: String,
        ): EventLogRecordModel {
            // TODO: Use a better entity Id
            return EventLogRecordModel(
                id = null,
                entityId = "${employeePk?.empId}_$timeRecorded",
                employeePk = employeePk,
                timeRecorded = timeRecorded,
                unit = unit.trim(),
                eventType = eventType ?: EventLogEventType.INCIDENT,
                fallbackEmployeeName = fallbackEmployeeName?.trim(),
                fallbackEventType = fallbackEventType?.trim(),
                title = title.trim(),
                description = description.trim(),
                attachments = emptyList(),
                propertyId = propertyId,
            )
        }
    }
}

/**
 * Holder for an attachment.
 */
data class AttachmentHolder(val publicUrl: String, val storageRef: String?)
