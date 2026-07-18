package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a unit.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A unit within a property.")
data class UnitNetworkResponse(
    @SerialName("unit_id")
    @JsonSchema.Description("Unique identifier of the unit.")
    val unitId: UnitId,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the unit belongs to.")
    val propertyId: PropertyId,
    @SerialName("unit_number")
    @JsonSchema.Description("Human-readable number or label of the unit.")
    @JsonSchema.Example("\"4B\"")
    val unitNumber: String,
    @SerialName("bedrooms")
    @JsonSchema.Description("Number of bedrooms in the unit, or null if unknown.")
    @JsonSchema.Minimum(0.0)
    val bedrooms: Int?,
    @SerialName("bathrooms")
    @JsonSchema.Description("Number of bathrooms in the unit, or null if unknown.")
    @JsonSchema.Minimum(0.0)
    val bathrooms: Int?,
    @SerialName("sq_ft")
    @JsonSchema.Description("Floor area of the unit in square feet, or null if unknown.")
    @JsonSchema.Minimum(0.0)
    val sqFt: Int?,
    @SerialName("floor")
    @JsonSchema.Description("Floor number the unit is located on, or null if unknown.")
    val floor: Int?,
    @SerialName("notes")
    @JsonSchema.Description("Freeform notes about the unit, or null if none.")
    val notes: String?,
) : ResponseBody
