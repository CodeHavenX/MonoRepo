package com.cramsan.edifikana.lib.firestore

import com.cramsan.edifikana.lib.requireNotBlank

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
@FireStoreModel
data class Employee(
    val id: String? = null,
    val idType: IdType? = null,
    val name: String? = null,
    val lastName: String? = null,
    val role: EmployeeRole? = null,
) {
    /**
     * Generates a document id based on the employee id and id type. This is used as the primary key in Firestore.
     */
    fun documentId(): EmployeePK {
        requireNotNull(id)
        require(id.isNotBlank())
        requireNotNull(idType)
        return EmployeePK("${idType?.name}_$id")
    }

    companion object {
        const val COLLECTION = "employees"
    }
}

@JvmInline
value class EmployeePK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
