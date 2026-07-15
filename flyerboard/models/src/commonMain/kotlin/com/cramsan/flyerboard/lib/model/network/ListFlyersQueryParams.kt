package com.cramsan.flyerboard.lib.model.network

import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing flyers, supporting pagination, optional status filtering,
 * and full-text search.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Query parameters for listing flyers, supporting pagination, optional status filtering, and full-text search.",
)
data class ListFlyersQueryParams(
    @SerialName("offset")
    @JsonSchema.Description("Number of results to skip.")
    @JsonSchema.Minimum(0.0)
    val offset: Int = 0,
    @SerialName("limit")
    @JsonSchema.Description("Maximum number of results to return.")
    @JsonSchema.Minimum(1.0)
    val limit: Int = 20,
    @SerialName("status")
    @JsonSchema.Description("Optional moderation status to filter by. Omit to include all statuses.")
    val status: FlyerStatus? = null,
    /** Optional search string. Matched case-insensitively against flyer title and description. */
    @SerialName("q")
    @JsonSchema.Description("Optional search string, matched case-insensitively against flyer title and description.")
    val q: String? = null,
) : QueryParam
