package com.cramsan.edifikana.lib.firestore


/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
data class EventLogRecord(
    val employeeDocumentId: String? = null,
    val timeRecorded: Long? = null,
    val eventType: TimeCardEventType? = null,
    val fallbackEmployeeName: String? = null,
    val fallbackEventType: String? = null,
) {

    fun documentId(): String {
        return "$employeeDocumentId-$timeRecorded-$eventType"
    }

    companion object {
        const val COLLECTION = "timeCardRecords"
    }
}