package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.NotificationApi
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.client.lib.service.NotificationService
import com.cramsan.edifikana.lib.model.network.notification.NotificationNetworkResponse
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient
import kotlin.time.ExperimentalTime

/**
 * Default implementation for the [NotificationService].
 */
@FrontendService
class NotificationServiceImpl(private val http: HttpClient) : NotificationService {
    override suspend fun getNotifications(): Result<List<Notification>> =
        runSuspendCatching(TAG) {
            val response = NotificationApi.getNotifications.buildRequest().execute(http)
            response.content.map { it.toNotification() }
        }

    override suspend fun getNotification(notificationId: NotificationId): Result<Notification> =
        runSuspendCatching(TAG) {
            val response =
                NotificationApi.getNotification
                    .buildRequest(
                        argument = notificationId,
                    ).execute(http)
            response.toNotification()
        }

    override suspend fun markAsRead(notificationId: NotificationId): Result<Notification> =
        runSuspendCatching(TAG) {
            val response =
                NotificationApi.markAsRead
                    .buildRequest(
                        argument = notificationId,
                    ).execute(http)
            response.toNotification()
        }

    override suspend fun deleteNotification(notificationId: NotificationId): Result<Unit> =
        runSuspendCatching(TAG) {
            NotificationApi.deleteNotification
                .buildRequest(
                    argument = notificationId,
                ).execute(http)
        }

    companion object {
        private const val TAG = "NotificationServiceImpl"
    }
}

@OptIn(ExperimentalTime::class)
private fun NotificationNetworkResponse.toNotification(): Notification {
    return Notification(
        id = this.id,
        type = this.notificationType,
        description = this.description,
        isRead = this.isRead,
        createdAt = this.createdAt,
        readAt = this.readAt,
        inviteId = this.inviteId,
    )
}
