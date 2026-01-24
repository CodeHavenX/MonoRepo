package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.client.lib.service.NotificationService
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for notifications.
 */
class NotificationManager(
    private val dependencies: ManagerDependencies,
    private val notificationService: NotificationService,
) {
    /**
     * Gets all notifications for the current user.
     */
    suspend fun getNotifications(): Result<List<Notification>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getNotifications")
        notificationService.getNotifications().getOrThrow()
    }

    /**
     * Gets a specific notification by ID.
     */
    suspend fun getNotification(notificationId: NotificationId): Result<Notification> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "getNotification: $notificationId")
            notificationService.getNotification(notificationId).getOrThrow()
        }

    /**
     * Marks a notification as read.
     */
    suspend fun markAsRead(notificationId: NotificationId): Result<Notification> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "markAsRead: $notificationId")
            notificationService.markAsRead(notificationId).getOrThrow()
        }

    /**
     * Deletes a notification.
     */
    suspend fun deleteNotification(notificationId: NotificationId): Result<Unit> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "deleteNotification: $notificationId")
            notificationService.deleteNotification(notificationId).getOrThrow()
        }

    companion object {
        private const val TAG = "NotificationManager"
    }
}
