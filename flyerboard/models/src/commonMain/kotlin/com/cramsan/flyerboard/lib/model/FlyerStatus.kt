package com.cramsan.flyerboard.lib.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the moderation status of a flyer.
 */
@Serializable
@JsonSchema.Description("Moderation status of a flyer.")
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
