package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new property.
 */
@NetworkModel
@Serializable
data class CreatePropertyNetworkRequest(
    @SerialName("name")
    val name: String,
)
