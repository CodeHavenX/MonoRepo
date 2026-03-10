package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of common areas.
 */
@NetworkModel
@Serializable
data class CommonAreaListNetworkResponse(
    @SerialName("common_areas")
    val commonAreas: List<CommonAreaNetworkResponse>,
) : ResponseBody
