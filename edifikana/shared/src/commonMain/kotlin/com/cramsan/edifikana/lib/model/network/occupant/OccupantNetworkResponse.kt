package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
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
data class OccupantNetworkResponse(
    @SerialName("occupant_id") val id: OccupantId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("user_id") val userId: UserId?,
    @SerialName("added_by") val addedBy: UserId?,
    val name: String,
    val email: String?,
    @SerialName("occupant_type") val occupantType: OccupantType,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate?,
    val status: OccupancyStatus,
    @SerialName("added_at") val addedAt: Instant,
) : ResponseBody
