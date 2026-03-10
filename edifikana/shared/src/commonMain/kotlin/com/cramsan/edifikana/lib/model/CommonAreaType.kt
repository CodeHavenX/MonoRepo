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

    companion object {
        /**
         * Converts a string value to a CommonAreaType.
         */
        fun fromString(value: String?): CommonAreaType {
            return when (value) {
                "LOBBY" -> LOBBY
                "POOL" -> POOL
                "GYM" -> GYM
                "PARKING" -> PARKING
                "LAUNDRY" -> LAUNDRY
                "ROOFTOP" -> ROOFTOP
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Invalid CommonAreaType value: $value")
            }
        }
    }
}
