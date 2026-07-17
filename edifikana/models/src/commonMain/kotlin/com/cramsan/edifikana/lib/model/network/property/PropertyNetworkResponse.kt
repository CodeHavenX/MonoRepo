package com.cramsan.edifikana.lib.model.network.property

import com.cramsan.edifikana.lib.model.common.Url
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A property owned by an organization.")
data class PropertyNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the property.")
    val id: PropertyId,
    @SerialName("name")
    @JsonSchema.Description("Human-readable display name of the property.")
    @JsonSchema.Example("\"Sunset Apartments\"")
    val name: String,
    @SerialName("address")
    @JsonSchema.Description("Physical street address of the property, if known.")
    @JsonSchema.Example("\"123 Main St, Springfield\"")
    val address: String? = null,
    @SerialName("organization_id")
    @JsonSchema.Description("Identifier of the organization that owns the property.")
    val organizationId: OrganizationId,
    @SerialName("image_url")
    @JsonSchema.Description("URL of the property's cover image, if set.")
    @JsonSchema.Format("uri")
    val imageUrl: Url? = null,
) : ResponseBody
