package com.cramsan.edifikana.lib.firestore

import com.cramsan.edifikana.lib.requireNotBlank

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
data class User(
    val id: String? = null,
) {
    /**
     * Generates a document id based on the employee id and id type. This is used as the primary key in Firestore.
     */
    fun documentId(): UserPk {
        requireNotNull(id)
        require(id.isNotBlank())
        return UserPk(id)
    }

    companion object {
        const val COLLECTION = "users"
    }
}

@JvmInline
value class UserPk(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
