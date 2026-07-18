package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing units.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing units, requiring a property id.")
data class GetUnitsQueryParams(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property to list units for.")
    val propertyId: PropertyId,
) : QueryParam
