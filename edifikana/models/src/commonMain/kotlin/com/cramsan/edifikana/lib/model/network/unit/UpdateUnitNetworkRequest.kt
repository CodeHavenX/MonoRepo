package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing unit.
 *
 * All fields are optional. A null value means "leave unchanged" — the existing database value
 * is preserved. It is not possible to explicitly clear a nullable field (e.g. reset [notes] to
 * null) through this request.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing unit. A null field means \"leave unchanged\"; it is not " +
        "possible to explicitly clear a nullable field through this request.",
)
data class UpdateUnitNetworkRequest(
    @SerialName("unit_number")
    @JsonSchema.Description("New number or label for the unit, or null to leave unchanged.")
    val unitNumber: String?,
    @SerialName("bedrooms")
    @JsonSchema.Description("New bedroom count for the unit, or null to leave unchanged.")
    @JsonSchema.Minimum(0.0)
    val bedrooms: Int?,
    @SerialName("bathrooms")
    @JsonSchema.Description("New bathroom count for the unit, or null to leave unchanged.")
    @JsonSchema.Minimum(0.0)
    val bathrooms: Int?,
    @SerialName("sq_ft")
    @JsonSchema.Description("New floor area for the unit in square feet, or null to leave unchanged.")
    @JsonSchema.Minimum(0.0)
    val sqFt: Int?,
    @SerialName("floor")
    @JsonSchema.Description("New floor number for the unit, or null to leave unchanged.")
    val floor: Int?,
    @SerialName("notes")
    @JsonSchema.Description("New freeform notes for the unit, or null to leave unchanged.")
    val notes: String?,
) : RequestBody
