package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to create a new unit occupant record.
 */
@NetworkModel
@Serializable
data class CreateOccupantNetworkRequest(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("user_id") val userId: UserId?,
    val name: String,
    val email: String?,
    @SerialName("occupant_type") val occupantType: OccupantType,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate?,
) : RequestBody
