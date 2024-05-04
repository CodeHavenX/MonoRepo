package com.cramsan.edifikana.lib.firestore


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
        return UserPk(id ?: TODO())
    }

    companion object {
        const val COLLECTION = "users"
    }
}

@JvmInline
value class UserPk(val documentPath: String)