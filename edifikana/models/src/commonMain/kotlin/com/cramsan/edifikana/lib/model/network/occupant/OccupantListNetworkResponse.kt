package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response containing a list of occupant records.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of occupant records.")
data class OccupantListNetworkResponse(
    @JsonSchema.Description("The occupant records matching the request.")
    val occupants: List<OccupantNetworkResponse>,
) : ResponseBody
