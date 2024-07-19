package com.cramsan.edifikana.lib.supa

import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.EventType
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class EventLogRecord(
    val pk: String,
    val employeeDocumentId: String? = null,
    val timeRecorded: Long? = null,
    val unit: String? = null,
    val eventType: EventType? = null,
    val fallbackEmployeeName: String? = null,
    val fallbackEventType: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val attachments: List<String>? = null,
) {
    companion object {
        const val COLLECTION = "eventLog"

        private fun documentId(
            employeeDocumentId: String?,
            timeRecorded: Long?,
            eventType: EventType?,
        ): EventLogRecordPK {
            requireNotNull(timeRecorded)
            require(timeRecorded > 0)
            requireNotNull(eventType)
            return EventLogRecordPK("$employeeDocumentId-$timeRecorded-$eventType")
        }

        fun create(
            employeeDocumentId: String?,
            timeRecorded: Long,
            unit: String,
            eventType: EventType,
            fallbackEmployeeName: String?,
            fallbackEventType: String?,
            summary: String,
            description: String,
            attachments: List<String>,
        ): EventLogRecord {
            return EventLogRecord(
                pk = documentId(employeeDocumentId, timeRecorded, eventType).documentPath,
                employeeDocumentId = employeeDocumentId,
                timeRecorded = timeRecorded,
                unit = unit,
                eventType = eventType,
                fallbackEmployeeName = fallbackEmployeeName,
                fallbackEventType = fallbackEventType,
                summary = summary,
                description = description,
                attachments = attachments,
            )
        }
    }
}
