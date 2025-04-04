package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update a property.
 */
@NetworkModel
@Serializable
data class UpdatePropertyNetworkRequest(
    @SerialName("name")
    val name: String?,
    @SerialName("address")
    val address: String?,
)
