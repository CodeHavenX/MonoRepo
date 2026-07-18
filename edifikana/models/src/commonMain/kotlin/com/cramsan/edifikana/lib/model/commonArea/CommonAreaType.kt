package com.cramsan.edifikana.lib.model.commonArea

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the type of common area within a property.
 */
@Serializable
@JsonSchema.Description("Type of a common area within a property.")
enum class CommonAreaType {
    @SerialName("LOBBY")
    LOBBY,

    @SerialName("POOL")
    POOL,

    @SerialName("GYM")
    GYM,

    @SerialName("PARKING")
    PARKING,

    @SerialName("LAUNDRY")
    LAUNDRY,

    @SerialName("ROOFTOP")
    ROOFTOP,

    @SerialName("OTHER")
    OTHER,
}
