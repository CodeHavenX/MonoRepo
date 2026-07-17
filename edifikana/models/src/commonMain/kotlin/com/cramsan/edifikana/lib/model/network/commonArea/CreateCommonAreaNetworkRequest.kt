package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new common area.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new common area within a property.")
data class CreateCommonAreaNetworkRequest(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the common area belongs to.")
    val propertyId: PropertyId,
    @SerialName("name")
    @JsonSchema.Description("Human-readable display name of the common area.")
    @JsonSchema.Example("\"Rooftop Pool\"")
    val name: String,
    @SerialName("type")
    @JsonSchema.Description("Type of the common area.")
    val type: CommonAreaType,
    @SerialName("description")
    @JsonSchema.Description("Description of the common area.")
    val description: String?,
) : RequestBody
