package com.cramsan.edifikana.lib.model.network.notification

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.notification.NotificationType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Response model for a notification.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A notification for the authenticated user.")
data class NotificationNetworkResponse(
    @JsonSchema.Description("Unique identifier of the notification.")
    val id: NotificationId,
    @JsonSchema.Description("Type of the notification.")
    val notificationType: NotificationType,
    @JsonSchema.Description("Human-readable description of the notification.")
    val description: String,
    @JsonSchema.Description("Whether the notification has been marked as read.")
    val isRead: Boolean,
    @JsonSchema.Description("ISO-8601 timestamp when the notification was created.")
    @JsonSchema.Format("date-time")
    val createdAt: Instant,
    @JsonSchema.Description("ISO-8601 timestamp when the notification was marked as read, or null if unread.")
    @JsonSchema.Format("date-time")
    val readAt: Instant?,
    @JsonSchema.Description(
        "Identifier of the related invite, or null if this notification is not invite-related.",
    )
    val inviteId: InviteId?,
) : ResponseBody
