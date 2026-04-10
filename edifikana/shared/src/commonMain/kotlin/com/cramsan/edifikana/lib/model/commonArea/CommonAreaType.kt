package com.cramsan.edifikana.lib.model.commonArea

import kotlinx.serialization.SerialName

/**
 * Represents the type of common area within a property.
 */
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
