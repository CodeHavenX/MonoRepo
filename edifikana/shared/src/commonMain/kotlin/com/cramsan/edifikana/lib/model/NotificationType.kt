package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Enum representing the type of notification.
 */
@Serializable
enum class NotificationType {
    /**
     * Notification for an organization invite.
     */
    INVITE,

    /**
     * System notification.
     */
    SYSTEM,
    ;

    companion object {
        /**
         * Converts a string to a NotificationType.
         */
        fun fromString(value: String): NotificationType = entries.find { it.name == value } ?: SYSTEM
    }
}
