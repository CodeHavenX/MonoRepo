package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for paginated requests.
 */
@NetworkModel
@Serializable
data class PaginationParams(
    @SerialName("offset")
    val offset: Int = 0,
    @SerialName("limit")
    val limit: Int = 20,
) : QueryParam
