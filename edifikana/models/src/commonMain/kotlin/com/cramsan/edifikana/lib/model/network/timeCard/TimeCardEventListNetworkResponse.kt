package com.cramsan.edifikana.lib.model.network.timeCard

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response body containing a list of time card events.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of time card events.")
data class TimeCardEventListNetworkResponse(
    @JsonSchema.Description("The time card events matching the request.")
    val events: List<TimeCardEventNetworkResponse>,
) : ResponseBody
