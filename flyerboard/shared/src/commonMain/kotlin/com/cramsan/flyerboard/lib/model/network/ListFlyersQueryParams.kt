package com.cramsan.flyerboard.lib.model.network

import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing flyers, supporting pagination, optional status filtering,
 * and full-text search.
 */
@NetworkModel
@Serializable
data class ListFlyersQueryParams(
    @SerialName("offset")
    val offset: Int = 0,
    @SerialName("limit")
    val limit: Int = 20,
    @SerialName("status")
    val status: FlyerStatus? = null,
    /** Optional search string. Matched case-insensitively against flyer title and description. */
    @SerialName("q")
    val q: String? = null,
) : QueryParam
