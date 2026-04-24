package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response containing a list of occupant records.
 */
@NetworkModel
@Serializable
data class OccupantListNetworkResponse(
    val occupants: List<OccupantNetworkResponse>,
) : ResponseBody
