package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request for updating a document's metadata.
 *
 * All fields are optional; only non-null fields will be applied.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing document's metadata. Only provided (non-null) fields are updated.",
)
data class UpdateDocumentNetworkRequest(
    @JsonSchema.Description("New filename, or null to leave unchanged.")
    val filename: String?,
    @SerialName("document_type")
    @JsonSchema.Description("New document category, or null to leave unchanged.")
    val documentType: DocumentType?,
) : RequestBody
