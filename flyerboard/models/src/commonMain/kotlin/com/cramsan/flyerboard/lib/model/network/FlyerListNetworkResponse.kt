package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a paginated list of flyers.
 */
@NetworkModel
@Serializable
data class FlyerListNetworkResponse(
    @SerialName("flyers")
    val flyers: List<FlyerNetworkResponse>,
    @SerialName("total")
    val total: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("limit")
    val limit: Int,
) : ResponseBody
