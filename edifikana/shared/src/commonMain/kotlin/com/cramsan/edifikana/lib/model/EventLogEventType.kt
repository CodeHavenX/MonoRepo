package com.cramsan.edifikana.lib.model

/**
 * Enum representing the type of event log.
 */
enum class EventLogEventType {
    GUEST,
    INCIDENT,
    DELIVERY,
    MAINTENANCE_SERVICE,
    OTHER,
    ;
    companion object {

        /**
         * Converts a string to an EventLogEventType.
         */
        fun fromString(value: String): EventLogEventType {
            return when (value) {
                "GUEST" -> GUEST
                "INCIDENT" -> INCIDENT
                "DELIVERY" -> DELIVERY
                "MAINTENANCE_SERVICE" -> MAINTENANCE_SERVICE
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Invalid EventType value: $value")
            }
        }
    }
}
