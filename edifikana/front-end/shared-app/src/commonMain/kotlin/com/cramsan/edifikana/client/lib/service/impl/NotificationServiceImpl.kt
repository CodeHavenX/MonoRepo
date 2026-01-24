package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.NotificationApi
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.client.lib.service.NotificationService
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.network.NotificationNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Default implementation for the [NotificationService].
 */
class NotificationServiceImpl(
    private val http: HttpClient,
) : NotificationService {

    @OptIn(NetworkModel::class)
    override suspend fun getNotifications(): Result<List<Notification>> = runSuspendCatching(TAG) {
        val response = NotificationApi.getNotifications.buildRequest().execute(http)
        response.content.map { it.toNotification() }
    }

    @OptIn(NetworkModel::class)
    override suspend fun getNotification(notificationId: NotificationId): Result<Notification> =
        runSuspendCatching(TAG) {
            val response = NotificationApi.getNotification.buildRequest(
                argument = notificationId
            ).execute(http)
            response.toNotification()
        }

    @OptIn(NetworkModel::class)
    override suspend fun markAsRead(notificationId: NotificationId): Result<Notification> =
        runSuspendCatching(TAG) {
            val response = NotificationApi.markAsRead.buildRequest(
                argument = notificationId
            ).execute(http)
            response.toNotification()
        }

    @OptIn(NetworkModel::class)
    override suspend fun deleteNotification(notificationId: NotificationId): Result<Unit> =
        runSuspendCatching(TAG) {
            NotificationApi.deleteNotification.buildRequest(
                argument = notificationId
            ).execute(http)
        }

    companion object {
        private const val TAG = "NotificationServiceImpl"
    }
}

@OptIn(NetworkModel::class, ExperimentalTime::class)
private fun NotificationNetworkResponse.toNotification(): Notification {
    return Notification(
        id = this.id,
        type = this.notificationType,
        description = this.description,
        isRead = this.isRead,
        createdAt = Instant.fromEpochSeconds(this.createdAt),
        readAt = this.readAt?.let { Instant.fromEpochSeconds(it) },
        inviteId = this.inviteId,
    )
}
