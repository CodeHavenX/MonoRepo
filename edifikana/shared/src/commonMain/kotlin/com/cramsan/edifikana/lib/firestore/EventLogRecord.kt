package com.cramsan.edifikana.lib.firestore

import com.cramsan.edifikana.lib.EventLogRecordPK
import kotlinx.serialization.Serializable

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
@Serializable
@FireStoreModel
data class EventLogRecord(
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
    fun documentId(): EventLogRecordPK {
        requireNotNull(timeRecorded)
        require(timeRecorded > 0)
        requireNotNull(eventType)
        return EventLogRecordPK("$employeeDocumentId-$timeRecorded-$eventType")
    }

    companion object {
        const val COLLECTION = "eventLog"
    }
}
