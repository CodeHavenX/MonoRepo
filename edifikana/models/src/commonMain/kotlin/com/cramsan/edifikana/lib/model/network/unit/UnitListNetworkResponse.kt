package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of units.
 */
@NetworkModel
@Serializable
data class UnitListNetworkResponse(
    @SerialName("units")
    val units: List<UnitNetworkResponse>,
) : ResponseBody
