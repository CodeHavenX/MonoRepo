package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a property.
 */
@NetworkModel
@Serializable
data class PropertyNetworkResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("address")
    val address: String? = null,
)
