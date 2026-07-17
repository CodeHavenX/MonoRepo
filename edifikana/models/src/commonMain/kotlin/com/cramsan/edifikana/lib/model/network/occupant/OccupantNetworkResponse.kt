package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network response for a unit occupant record.
 */
@OptIn(ExperimentalTime::class)
@NetworkModel
@Serializable
@JsonSchema.Description("An occupant (tenant or resident) associated with a unit.")
data class OccupantNetworkResponse(
    @SerialName("occupant_id")
    @JsonSchema.Description("Unique identifier of the occupant record.")
    val id: OccupantId,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the occupant is associated with.")
    val unitId: UnitId,
    @SerialName("user_id")
    @JsonSchema.Description("Identifier of the linked user account, or null if the occupant has no account.")
    val userId: UserId?,
    @SerialName("added_by")
    @JsonSchema.Description("Identifier of the user who added this occupant record, or null if unknown.")
    val addedBy: UserId?,
    @JsonSchema.Description("Full name of the occupant.")
    @JsonSchema.Example("\"Jane Doe\"")
    val name: String,
    @JsonSchema.Description("Email address of the occupant, or null if not provided.")
    val email: String?,
    @SerialName("occupant_type")
    @JsonSchema.Description("Occupancy type of the occupant.")
    val occupantType: OccupantType,
    @SerialName("is_primary")
    @JsonSchema.Description("Whether this occupant is the primary occupant of the unit.")
    val isPrimary: Boolean,
    @SerialName("start_date")
    @JsonSchema.Description("Date the occupancy started.")
    @JsonSchema.Format("date")
    val startDate: LocalDate,
    @SerialName("end_date")
    @JsonSchema.Description("Date the occupancy ended, or null if it is ongoing.")
    @JsonSchema.Format("date")
    val endDate: LocalDate?,
    @JsonSchema.Description("Current occupancy status of the record.")
    val status: OccupancyStatus,
    @SerialName("added_at")
    @JsonSchema.Description("ISO-8601 timestamp when the occupant record was added.")
    @JsonSchema.Format("date-time")
    val addedAt: Instant,
) : ResponseBody
