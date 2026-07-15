package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a paginated list of flyers.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A paginated collection of flyers.")
data class FlyerListNetworkResponse(
    @SerialName("flyers")
    @JsonSchema.Description("The flyers in this page of results.")
    val flyers: List<FlyerNetworkResponse>,
    @SerialName("total")
    @JsonSchema.Description("Total number of flyers matching the query, across all pages.")
    val total: Int,
    @SerialName("offset")
    @JsonSchema.Description("Offset of this page within the total result set.")
    val offset: Int,
    @SerialName("limit")
    @JsonSchema.Description("Maximum number of flyers requested for this page.")
    val limit: Int,
) : ResponseBody
