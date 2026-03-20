package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.DocumentType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request for updating a document's metadata.
 *
 * All fields are optional; only non-null fields will be applied.
 */
@NetworkModel
@Serializable
data class UpdateDocumentNetworkRequest(
    val filename: String?,
    @SerialName("document_type") val documentType: DocumentType?,
) : RequestBody
