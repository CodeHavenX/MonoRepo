package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.NotificationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.framework.logging.logD

/**
 * Service for managing notifications.
 * Handles creation, retrieval, and management of notifications.
 */
class NotificationService(
    private val notificationDatastore: NotificationDatastore,
    private val userDatastore: UserDatastore,
) {

    /**
     * Creates an invitation notification.
     *
     * @param recipientEmail The email address of the recipient
     * @param organizationId The organization the user is being invited to
     * @return Result containing the created notification
     */
    suspend fun createInviteNotification(
        recipientEmail: String,
        organizationId: OrganizationId,
    ): Result<Notification> {
        logD(TAG, "Creating invite notification for: $recipientEmail")

        val userId = userDatastore.getUser(recipientEmail).getOrNull()?.id

        return notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = recipientEmail,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        )
    }

    /**
     * Retrieves notifications for a user.
     *
     * @param userId The user ID
     * @param unreadOnly If true, only returns unread notifications
     * @param limit Maximum number of notifications to return
     * @return Result containing the list of notifications
     */
    suspend fun getNotificationsForUser(
        userId: UserId,
        unreadOnly: Boolean = false,
        limit: Int? = null,
    ): Result<List<Notification>> {
        logD(TAG, "Getting notifications for user: $userId")
        return notificationDatastore.getNotificationsForUser(userId, unreadOnly, limit)
    }

    /**
     * Retrieves a single notification by ID.
     *
     * @param notificationId The notification ID
     * @return Result containing the notification if found
     */
    suspend fun getNotification(
        notificationId: NotificationId,
    ): Result<Notification?> {
        logD(TAG, "Getting notification: $notificationId")
        return notificationDatastore.getNotification(notificationId)
    }

    /**
     * Marks a notification as read.
     *
     * @param notificationId The notification ID
     * @return Result containing the updated notification
     */
    suspend fun markAsRead(
        notificationId: NotificationId,
    ): Result<Notification> {
        logD(TAG, "Marking notification as read: $notificationId")
        return notificationDatastore.markAsRead(notificationId)
    }

    /**
     * Deletes a notification.
     *
     * @param notificationId The notification ID
     * @return Result indicating success
     */
    suspend fun deleteNotification(
        notificationId: NotificationId,
    ): Result<Boolean> {
        logD(TAG, "Deleting notification: $notificationId")
        return notificationDatastore.deleteNotification(notificationId)
    }

    /**
     * Links all notifications with a given email to a user ID.
     * This should be called when a user signs up or associates their account.
     *
     * @param email The email address
     * @param userId The user ID to link notifications to
     * @return Result containing the number of notifications updated
     */
    suspend fun linkNotificationsToUser(
        email: String,
        userId: UserId,
    ): Result<Int> {
        logD(TAG, "Linking notifications for email: $email to user: $userId")
        return notificationDatastore.linkNotificationsToUser(email, userId)
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
