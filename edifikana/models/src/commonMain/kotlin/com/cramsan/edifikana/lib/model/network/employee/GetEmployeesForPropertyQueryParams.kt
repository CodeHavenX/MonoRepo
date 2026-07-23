package com.cramsan.edifikana.lib.model.network.employee

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing employees assigned to a property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing employees assigned to a property, requiring a property id.")
data class GetEmployeesForPropertyQueryParams(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property to list employees for.")
    val propertyId: PropertyId,
) : QueryParam
