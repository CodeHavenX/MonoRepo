package com.cramsan.edifikana.lib.model.network.property

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new property within an organization.")
data class CreatePropertyNetworkRequest(
    @SerialName("name")
    @JsonSchema.Description("Human-readable display name of the property.")
    @JsonSchema.Example("\"Sunset Apartments\"")
    val name: String,
    @SerialName("address")
    @JsonSchema.Description("Physical street address of the property.")
    @JsonSchema.Example("\"123 Main St, Springfield\"")
    val address: String,
    @SerialName("organization_id")
    @JsonSchema.Description("Identifier of the organization that will own the property.")
    val organizationId: OrganizationId,
    @SerialName("image_url")
    @JsonSchema.Description("Optional URL of a cover image for the property.")
    @JsonSchema.Format("uri")
    @JsonSchema.Example("\"https://cdn.example.com/properties/sunset.jpg\"")
    val imageUrl: String? = null,
) : RequestBody
