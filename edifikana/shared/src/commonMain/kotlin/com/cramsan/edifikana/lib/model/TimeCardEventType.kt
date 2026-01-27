package com.cramsan.edifikana.lib.model

/**
 * Enum representing the type of time card event.
 */
enum class TimeCardEventType {
    CLOCK_IN,
    CLOCK_OUT,
    OTHER,
    ;

    companion object {

        /**
         * Converts a string to a TimeCardEventType.
         */
        fun fromString(value: String?): TimeCardEventType = when (value) {
            "CLOCK_IN" -> CLOCK_IN
            "CLOCK_OUT" -> CLOCK_OUT
            "OTHER" -> OTHER
            else -> throw IllegalArgumentException("Invalid TimeCardEventType value: $value")
        }
    }
}
