package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a property.
 */
@NetworkModel
@Serializable
data class PropertyResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
)
