package com.cramsan.edifikana.lib.model.notification

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
