package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new property.
 */
@NetworkModel
@Serializable
data class CreateStaffNetworkRequest(
    @SerialName("name")
    val name: String,
    @SerialName("property_id")
    val propertyId: Int,
)
