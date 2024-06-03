package com.cramsan.edifikana.lib.firestore

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 *
 * To query this model from Firestore we will need to have a query.
 * https://console.firebase.google.com/v1/r/project/edifikana/firestore/indexes?create_composite=ClFwcm9qZWN0cy9lZGlmaWthbmEvZGF0YWJhc2VzLyhkZWZhdWx0KS9jb2xsZWN0aW9uR3JvdXBzL3RpbWVDYXJkUmVjb3Jkcy9pbmRleGVzL18QARoWChJlbXBsb3llZURvY3VtZW50SWQQARoQCgx0aW1lUmVjb3JkZWQQAhoMCghfX25hbWVfXxAC
 */
@FireStoreModel
data class TimeCardRecord(
    val employeeDocumentId: String? = null,
    val eventType: TimeCardEventType? = null,
    val eventTime: Long? = null,
    val imageUrl: String? = null,
) {

    fun documentId(): TimeCardRecordPK {
        requireNotNull(employeeDocumentId)
        require(employeeDocumentId.isNotBlank())
        requireNotNull(eventType)
        requireNotNull(eventTime)
        require(eventTime > 0)
        return TimeCardRecordPK("$employeeDocumentId-$eventType-$eventTime")
    }

    companion object {
        const val COLLECTION = "timeCardRecords"
    }
}

@JvmInline
value class TimeCardRecordPK(val documentPath: String) {
    override fun toString() = documentPath
}
