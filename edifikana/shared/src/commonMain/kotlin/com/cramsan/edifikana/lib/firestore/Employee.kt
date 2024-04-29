package com.cramsan.edifikana.lib.firestore


/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
data class Employee(
    val id: String? = null,
    val idType: IdType? = null,
    val name: String? = null,
    val lastName: String? = null,
) {

    /**
     * Generates a document id based on the employee id and id type. This is used as the primary key in Firestore.
     */
    fun documentId(): String {
        return "${idType?.name}_${id}"
    }

    companion object {
        const val COLLECTION = "employees"
    }
}