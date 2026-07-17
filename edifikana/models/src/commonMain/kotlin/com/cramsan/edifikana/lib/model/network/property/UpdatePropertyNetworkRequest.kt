package com.cramsan.edifikana.lib.model.network.property

import com.cramsan.edifikana.lib.model.common.Url
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update a property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing property. Only the fields that are provided " +
        "(non-null) are modified.",
)
data class UpdatePropertyNetworkRequest(
    @SerialName("name")
    @JsonSchema.Description("New display name for the property, or null to leave unchanged.")
    @JsonSchema.Example("\"Sunset Apartments\"")
    val name: String?,
    @SerialName("address")
    @JsonSchema.Description("New street address for the property, or null to leave unchanged.")
    @JsonSchema.Example("\"123 Main St, Springfield\"")
    val address: String?,
    @SerialName("image_url")
    @JsonSchema.Description("New cover image URL, or null to leave unchanged.")
    @JsonSchema.Format("uri")
    val imageUrl: Url? = null,
) : RequestBody
