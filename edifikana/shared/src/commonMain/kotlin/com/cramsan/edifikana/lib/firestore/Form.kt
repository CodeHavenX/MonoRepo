package com.cramsan.edifikana.lib.firestore

import kotlin.math.min

@FireStoreModel
data class Form(
    val name: String? = null,
    val propertyId: String? = null,
    val fields: List<FormField>? = null,
) {
    /**
     * Generates a document id based on the form name and property id.
     */
    fun documentId(): FormPK {
        val encodedFormName = name
            ?.substring(0, min(name.length, 50))
            ?.replace(nonAlphaNum, "_")
            ?.lowercase() ?: TODO()
        return FormPK("${encodedFormName}_$propertyId")
    }

    companion object {
        const val COLLECTION = "forms"
    }
}

@FireStoreModel
data class FormField(
    val id: String? = null,
    val name: String? = null,
    val required: Boolean? = null,
    val isSingleLine: Boolean? = null,
)

@JvmInline
value class FormPK(val documentPath: String)

private val nonAlphaNum = "[^a-zA-Z0-9]".toRegex()
