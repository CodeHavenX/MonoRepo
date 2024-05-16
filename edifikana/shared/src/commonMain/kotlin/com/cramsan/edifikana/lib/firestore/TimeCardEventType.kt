package com.cramsan.edifikana.lib.firestore

enum class TimeCardEventType {
    CLOCK_IN,
    CLOCK_OUT,
    OTHER,
    ;
    companion object {
        fun fromString(value: String): TimeCardEventType {
            return when (value) {
                "CLOCK_IN" -> CLOCK_IN
                "CLOCK_OUT" -> CLOCK_OUT
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Invalid TimeCardEventType value: $value")
            }
        }
    }
}
