package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for paginated requests.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for paginated requests.")
data class PaginationParams(
    @SerialName("offset")
    @JsonSchema.Description("Number of results to skip.")
    @JsonSchema.Minimum(0.0)
    val offset: Int = 0,
    @SerialName("limit")
    @JsonSchema.Description("Maximum number of results to return.")
    @JsonSchema.Minimum(1.0)
    val limit: Int = 20,
) : QueryParam
