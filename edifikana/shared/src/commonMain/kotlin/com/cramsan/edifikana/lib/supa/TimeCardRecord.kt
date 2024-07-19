package com.cramsan.edifikana.lib.supa

import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class TimeCardRecord(
    val pk: String,
    val employeeDocumentId: String? = null,
    val eventType: TimeCardEventType? = null,
    val eventTime: Long? = null,
    val imageUrl: String? = null,
) {
    companion object {
        const val COLLECTION = "timeCardRecords"

        private fun documentId(
            employeeDocumentId: String?,
            eventType: TimeCardEventType?,
            eventTime: Long?,
        ): TimeCardRecordPK {
            requireNotNull(employeeDocumentId)
            require(employeeDocumentId.isNotBlank())
            requireNotNull(eventType)
            requireNotNull(eventTime)
            require(eventTime > 0)
            return TimeCardRecordPK("$employeeDocumentId-$eventType-$eventTime")
        }

        fun create(
            employeeDocumentId: String,
            eventType: TimeCardEventType,
            eventTime: Long,
            imageUrl: String?,
        ): TimeCardRecord {
            return TimeCardRecord(
                pk = documentId(employeeDocumentId, eventType, eventTime).documentPath,
                employeeDocumentId = employeeDocumentId,
                eventType = eventType,
                eventTime = eventTime,
                imageUrl = imageUrl,
            )
        }
    }
}
