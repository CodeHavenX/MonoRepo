package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a staff.
 */
@NetworkModel
@Serializable
data class TimeCardEventResponse(
    @SerialName("id")
    val id: String,
    @SerialName("staff_id")
    val staffId: String,
    @SerialName("type")
    val type: String,
    @SerialName("time")
    val time: Long,
)