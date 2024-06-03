package com.cramsan.edifikana.lib.firestore

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
@FireStoreModel
data class PropertyConfig(
    val propertyId: String? = null,
    val driveFolderId: String? = null,
    val storageFolderId: String? = null,
    val timeCardSpreadsheetId: String? = null,
    val eventLogSpreadsheetId: String? = null,
    val formEntriesSpreadsheetId: String? = null,
    val timeZone: String? = null,
) {
    /**
     * Generates a document id based on the employee id and id type. This is used as the primary key in Firestore.
     */
    fun documentId(): PropertyConfigPK {
        requireNotNull(propertyId)
        require(propertyId.isNotBlank())
        return PropertyConfigPK(propertyId)
    }

    companion object {
        const val COLLECTION = "property_configs"
    }
}

@JvmInline
value class PropertyConfigPK(val documentPath: String)
