package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of common areas.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of common areas.")
data class CommonAreaListNetworkResponse(
    @SerialName("common_areas")
    @JsonSchema.Description("The common areas matching the request.")
    val commonAreas: List<CommonAreaNetworkResponse>,
) : ResponseBody
