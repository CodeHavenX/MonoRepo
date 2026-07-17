package com.cramsan.edifikana.lib.model.timeCard

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Enum representing the type of time card event.
 */
@Serializable
@JsonSchema.Description("Type of a time card event.")
enum class TimeCardEventType {
    CLOCK_IN,
    CLOCK_OUT,
    OTHER,
    ;

    companion object {
        /**
         * Converts a string to a TimeCardEventType.
         */
        fun fromString(value: String?): TimeCardEventType {
            return when (value) {
                "CLOCK_IN" -> CLOCK_IN
                "CLOCK_OUT" -> CLOCK_OUT
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Invalid TimeCardEventType value: $value")
            }
        }
    }
}
