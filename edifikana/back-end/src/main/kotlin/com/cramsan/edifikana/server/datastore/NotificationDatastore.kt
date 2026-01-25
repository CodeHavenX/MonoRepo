package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.Notification

/**
 * Interface for interacting with the notification database.
 */
interface NotificationDatastore {

    /**
     * Creates a new notification.
     * If [recipientUserId] is null, [recipientEmail] should be provided for later association.
     * Returns the [Result] of the operation with the created [Notification].
     */
    suspend fun createNotification(
        recipientUserId: UserId?,
        recipientEmail: String?,
        notificationType: NotificationType,
        description: String,
        inviteId: InviteId,
    ): Result<Notification>

    /**
     * Retrieves a notification by ID.
     * Returns the [Result] of the operation with the fetched [Notification] if found.
     */
    suspend fun getNotification(
        id: NotificationId,
    ): Result<Notification?>

    /**
     * Retrieves a notification by associated invite ID.
     * Returns the [Result] of the operation with the fetched [Notification] if found.
     */
    suspend fun getNotificationByInvite(
        inviteId: InviteId,
    ): Result<Notification?>

    /**
     * Retrieves all notifications for a user by user ID.
     * Returns the [Result] of the operation with a list of [Notification].
     */
    suspend fun getNotificationsForUser(
        userId: UserId,
        unreadOnly: Boolean = false,
        limit: Int? = null,
    ): Result<List<Notification>>

    /**
     * Retrieves all notifications for a user by email.
     * Returns the [Result] of the operation with a list of [Notification].
     */
    suspend fun getNotificationsByEmail(
        email: String,
    ): Result<List<Notification>>

    /**
     * Marks a notification as read.
     * Returns the [Result] of the operation with the updated [Notification].
     */
    suspend fun markAsRead(
        id: NotificationId,
    ): Result<Notification>

    /**
     * Deletes a notification.
     * Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteNotification(
        id: NotificationId,
    ): Result<Boolean>

    /**
     * Links all notifications with a given email to a user ID.
     * This is called when a user signs up or associates their account.
     * Returns the [Result] of the operation with the number of notifications updated.
     */
    suspend fun linkNotificationsToUser(
        email: String,
        userId: UserId,
    ): Result<Int>
}
