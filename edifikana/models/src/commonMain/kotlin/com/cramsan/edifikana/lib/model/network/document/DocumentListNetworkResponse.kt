package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response for a list of documents.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of documents.")
data class DocumentListNetworkResponse(
    @JsonSchema.Description("The documents matching the request.")
    val documents: List<DocumentNetworkResponse>,
) : ResponseBody
