package com.cramsan.edifikana.lib.model.network.property

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of properties.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A collection of properties assigned to the authenticated user.")
data class PropertyListNetworkResponse(
    @SerialName("properties")
    @JsonSchema.Description("The properties the user has access to. May be empty.")
    val properties: List<PropertyNetworkResponse>,
) : ResponseBody
