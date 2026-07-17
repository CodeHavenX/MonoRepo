package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of units.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of units.")
data class UnitListNetworkResponse(
    @SerialName("units")
    @JsonSchema.Description("The units matching the request.")
    val units: List<UnitNetworkResponse>,
) : ResponseBody
