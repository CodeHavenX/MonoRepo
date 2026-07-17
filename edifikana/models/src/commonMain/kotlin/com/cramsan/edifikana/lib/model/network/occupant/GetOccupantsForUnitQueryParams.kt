package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing occupants for a unit.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing occupants of a unit.")
data class GetOccupantsForUnitQueryParams(
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit to list occupants for.")
    val unitId: UnitId,
    @SerialName("include_inactive")
    @JsonSchema.Description("Whether to include occupants with INACTIVE status. Defaults to false.")
    val includeInactive: Boolean = false,
) : QueryParam
