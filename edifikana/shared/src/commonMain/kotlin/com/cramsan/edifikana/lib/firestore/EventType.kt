package com.cramsan.edifikana.lib.firestore

enum class EventType {
    GUEST,
    INCIDENT,
    DELIVERY,
    MAINTENANCE_SERVICE,
    OTHER,
    ;
    companion object {
        fun fromString(value: String): EventType {
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
