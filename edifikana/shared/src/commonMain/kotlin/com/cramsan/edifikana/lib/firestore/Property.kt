package com.cramsan.edifikana.lib.firestore

import com.cramsan.edifikana.lib.requireNotBlank
import kotlinx.serialization.Serializable

/**
 * Due to Firestore limitations, we need to make all fields nullable and with a default value.
 */
@Serializable
@FireStoreModel
data class Property(
    val id: String? = null,
    val name: String? = null,
    val address: String? = null,
) {
    /**
     * Generates a document id based on the property id. This is used as the primary key in Firestore.
     */
    fun documentId(): PropertyPK {
        requireNotNull(id)
        require(id.isNotBlank())
        return PropertyPK(id)
    }

    companion object {
        const val COLLECTION = "properties"
    }
}

@JvmInline
value class PropertyPK(val documentPath: String) {
    init {
        requireNotBlank(documentPath)
    }
    override fun toString() = documentPath
}
