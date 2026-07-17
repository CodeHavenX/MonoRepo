package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to create a new unit occupant record.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new occupant record for a unit.")
data class CreateOccupantNetworkRequest(
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the occupant is associated with.")
    val unitId: UnitId,
    @SerialName("user_id")
    @JsonSchema.Description("Identifier of the linked user account, or null if the occupant has no account.")
    val userId: UserId?,
    @JsonSchema.Description("Full name of the occupant.")
    @JsonSchema.Example("\"Jane Doe\"")
    val name: String,
    @JsonSchema.Description("Email address of the occupant.")
    val email: Email?,
    @SerialName("occupant_type")
    @JsonSchema.Description("Occupancy type of the occupant.")
    val occupantType: OccupantType,
    @SerialName("is_primary")
    @JsonSchema.Description("Whether this occupant is the primary occupant of the unit.")
    val isPrimary: Boolean,
    @SerialName("start_date")
    @JsonSchema.Description("Date the occupancy starts.")
    @JsonSchema.Format("date")
    val startDate: LocalDate,
    @SerialName("end_date")
    @JsonSchema.Description("Date the occupancy ends, or null if open-ended.")
    @JsonSchema.Format("date")
    val endDate: LocalDate?,
) : RequestBody
