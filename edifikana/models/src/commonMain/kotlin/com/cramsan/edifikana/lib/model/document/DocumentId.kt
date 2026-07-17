package com.cramsan.edifikana.lib.model.document

import com.cramsan.framework.annotations.api.PathParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a document ID.
 */
@JvmInline
@Serializable
@JsonSchema.Description("Unique identifier of a document.")
@JsonSchema.Example("\"doc_a1b2c3d4\"")
value class DocumentId(val documentId: String) : PathParam {
    override fun toString(): String = documentId
}
