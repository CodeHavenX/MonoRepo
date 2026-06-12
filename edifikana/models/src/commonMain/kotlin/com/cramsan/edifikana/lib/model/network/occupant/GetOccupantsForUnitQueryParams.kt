package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing occupants for a unit.
 */
@NetworkModel
@Serializable
data class GetOccupantsForUnitQueryParams(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("include_inactive") val includeInactive: Boolean = false,
) : QueryParam
