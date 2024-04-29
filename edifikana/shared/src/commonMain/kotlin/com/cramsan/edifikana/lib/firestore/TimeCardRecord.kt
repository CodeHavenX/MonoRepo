package com.cramsan.edifikana.lib.firestore


/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
data class TimeCardRecord(
    val employeeDocumentId: String? = null,
    val eventType: TimeCardEventType? = null,
    val timeRecorded: Long? = null,
    val fallbackEmployeeName: String? = null,
    val fallbackEmployeeIdType: IdType? = null,
    val fallbackEmployeeIdTypeOther: String? = null,
    val fallbackEmployeeIdReason: String? = null,
) {

    fun documentId(): String {
        return "$employeeDocumentId-$eventType-$timeRecorded"
    }

    companion object {
        const val COLLECTION = "timeCardRecords"
    }
}