package com.cramsan.edifikana.lib.firestore

@FireStoreModel
data class FormRecord(
    val propertyId: String? = null,
    val formPk: String? = null,
    val name: String? = null,
    val timeRecorded: Long? = null,
    val fields: List<FormEntryField>? = null,
) {
    /**
     * Generates a document id based on the form name and property id.
     */
    fun formRecordPK(): FormRecordPK {
        return FormRecordPK("${propertyId}_${formPk}_$timeRecorded")
    }

    companion object {
        const val COLLECTION = "form_records"
    }
}

@FireStoreModel
data class FormEntryField(
    val id: String? = null,
    val name: String? = null,
    val value: String? = null,
)

@JvmInline
value class FormRecordPK(val documentPath: String)
