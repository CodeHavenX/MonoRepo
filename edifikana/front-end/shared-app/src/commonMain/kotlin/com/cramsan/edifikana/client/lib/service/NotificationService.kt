package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.NotificationId

/**
 * Service for managing notifications.
 */
interface NotificationService {

    /**
     * Get all notifications for the authenticated user.
     */
    suspend fun getNotifications(): Result<List<Notification>>

    /**
     * Get a specific notification by ID.
     */
    suspend fun getNotification(notificationId: NotificationId): Result<Notification>

    /**
     * Mark a notification as read.
     */
    suspend fun markAsRead(notificationId: NotificationId): Result<Notification>

    /**
     * Delete a notification.
     */
    suspend fun deleteNotification(notificationId: NotificationId): Result<Unit>
}
