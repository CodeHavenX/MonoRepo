package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a common area.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A shared space within a property, e.g. a lobby, pool, or gym.")
data class CommonAreaNetworkResponse(
    @SerialName("common_area_id")
    @JsonSchema.Description("Unique identifier of the common area.")
    val commonAreaId: CommonAreaId,
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
    @JsonSchema.Description("Description of the common area, or null if none.")
    val description: String?,
) : ResponseBody
