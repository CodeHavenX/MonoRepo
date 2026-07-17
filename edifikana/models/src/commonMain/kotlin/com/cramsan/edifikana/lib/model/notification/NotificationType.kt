package com.cramsan.edifikana.lib.model.notification

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Enum representing the type of notification.
 */
@Serializable
@JsonSchema.Description("Type of a notification.")
enum class NotificationType {
    // Notification for an organization invite.
    INVITE,

    // System notification.
    SYSTEM,
}
