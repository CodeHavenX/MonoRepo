package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a time card event.
 */
@NetworkModel
@Serializable
data class CreateTimeCardEventNetworkRequest(
    @SerialName("staff_id")
    val staffId: String,
    @SerialName("type")
    val type: String,
    @SerialName("time")
    val time: Long,

)
