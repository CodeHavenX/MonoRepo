package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Enum representing the type of notification.
 */
@Serializable
enum class NotificationType {
    // Notification for an organization invite.
    INVITE,
    // System notification.
    SYSTEM,
    ;
}
