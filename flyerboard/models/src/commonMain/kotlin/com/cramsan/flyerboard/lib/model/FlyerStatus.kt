package com.cramsan.flyerboard.lib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the moderation status of a flyer.
 */
@Serializable
enum class FlyerStatus {
    @SerialName("pending")
    PENDING,

    @SerialName("approved")
    APPROVED,

    @SerialName("rejected")
    REJECTED,

    @SerialName("archived")
    ARCHIVED,
}
