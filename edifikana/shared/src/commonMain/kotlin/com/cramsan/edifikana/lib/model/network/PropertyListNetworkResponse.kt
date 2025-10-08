package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a list of properties.
 */
@NetworkModel
@Serializable
data class PropertyListNetworkResponse(
    @SerialName("properties")
    val properties: List<PropertyNetworkResponse>,
) : ResponseBody
