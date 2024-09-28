package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update a property.
 */
@NetworkModel
@Serializable
data class UpdatePropertyRequest(
    @SerialName("name")
    val name: String,
)
