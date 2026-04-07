package com.cramsan.edifikana.lib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the type of a common area within a property.
 */
@Serializable
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
    ;
}
