package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to update an existing unit occupant record. Only provided (non-null) fields are updated.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing occupant record. Only provided (non-null) fields are updated.",
)
data class UpdateOccupantNetworkRequest(
    @JsonSchema.Description("New name for the occupant, or null to leave unchanged.")
    val name: String?,
    @JsonSchema.Description("New email address for the occupant, or null to leave unchanged.")
    val email: String?,
    @SerialName("occupant_type")
    @JsonSchema.Description("New occupancy type for the occupant, or null to leave unchanged.")
    val occupantType: OccupantType?,
    @SerialName("is_primary")
    @JsonSchema.Description("New primary-occupant flag, or null to leave unchanged.")
    val isPrimary: Boolean?,
    @SerialName("end_date")
    @JsonSchema.Description("New occupancy end date, or null to leave unchanged.")
    @JsonSchema.Format("date")
    val endDate: LocalDate?,
    @JsonSchema.Description("New occupancy status, or null to leave unchanged.")
    val status: OccupancyStatus?,
) : RequestBody
