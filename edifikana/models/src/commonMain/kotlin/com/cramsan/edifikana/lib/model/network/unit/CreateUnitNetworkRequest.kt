package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new unit.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new unit within a property.")
data class CreateUnitNetworkRequest(
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the unit belongs to.")
    val propertyId: PropertyId,
    @SerialName("unit_number")
    @JsonSchema.Description("Human-readable number or label of the unit.")
    @JsonSchema.Example("\"4B\"")
    val unitNumber: String,
    @SerialName("bedrooms")
    @JsonSchema.Description("Number of bedrooms in the unit.")
    @JsonSchema.Minimum(0.0)
    val bedrooms: Int?,
    @SerialName("bathrooms")
    @JsonSchema.Description("Number of bathrooms in the unit.")
    @JsonSchema.Minimum(0.0)
    val bathrooms: Int?,
    @SerialName("sq_ft")
    @JsonSchema.Description("Floor area of the unit in square feet.")
    @JsonSchema.Minimum(0.0)
    val sqFt: Int?,
    @SerialName("floor")
    @JsonSchema.Description("Floor number the unit is located on.")
    val floor: Int?,
    @SerialName("notes")
    @JsonSchema.Description("Freeform notes about the unit.")
    val notes: String?,
) : RequestBody
