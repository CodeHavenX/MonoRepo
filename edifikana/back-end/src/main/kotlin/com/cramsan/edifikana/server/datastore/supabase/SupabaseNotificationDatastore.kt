package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.NotificationDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.NotificationEntity
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing notifications using Supabase.
 */
@OptIn(SupabaseModel::class)
class SupabaseNotificationDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : NotificationDatastore {

    override suspend fun createNotification(
        recipientUserId: UserId?,
        notificationType: NotificationType,
        description: String,
        inviteId: InviteId,
    ): Result<Notification> = runSuspendCatching(TAG) {
        logD(TAG, "Creating notification for email: $recipientUserId")

        val createEntity = NotificationEntity.Create(
            recipientUserId = recipientUserId?.userId,
            notificationType = notificationType.name,
            description = description,
            inviteId = inviteId.id,
        )

        val createdEntity = postgrest.from(NotificationEntity.COLLECTION).insert(createEntity) {
            select()
        }.decodeSingle<NotificationEntity>()

        createdEntity.toNotification()
    }

    override suspend fun getNotification(
        id: NotificationId,
    ): Result<Notification?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notification: $id")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                eq("id", id.id)
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
    }

    override suspend fun getNotificationByInvite(inviteId: InviteId): Result<Notification?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notification by invite: $inviteId")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                eq("invite_id", inviteId.id)
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
    }

    override suspend fun getNotificationsForUser(
        userId: UserId,
        unreadOnly: Boolean,
        limit: Int?,
    ): Result<List<Notification>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notifications for user: $userId, unreadOnly: $unreadOnly")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                eq("recipient_user_id", userId.userId)
                if (unreadOnly) {
                    eq("is_read", false)
                }
            }
            order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit?.let { limit(it.toLong()) }
        }.decodeList<NotificationEntity>().map { it.toNotification() }
    }

    override suspend fun getNotificationsByEmail(
        email: String,
    ): Result<List<Notification>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notifications for email: $email")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                eq("recipient_email", email)
            }
            order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<NotificationEntity>().map { it.toNotification() }
    }

    override suspend fun markAsRead(
        id: NotificationId,
    ): Result<Notification> = runSuspendCatching(TAG) {
        logD(TAG, "Marking notification as read: $id")

        postgrest.from(NotificationEntity.COLLECTION).update(
            {
                NotificationEntity::isRead setTo true
                NotificationEntity::readAt setTo clock.now()
            }
        ) {
            select()
            filter {
                eq("id", id.id)
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
            ?: throw ClientRequestExceptions.NotFoundException("Notification not found: $id")
    }

    override suspend fun deleteNotification(
        id: NotificationId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting notification: $id")

        postgrest.from(NotificationEntity.COLLECTION).delete {
            select()
            filter {
                eq("id", id.id)
            }
        }.decodeSingleOrNull<NotificationEntity>() != null
    }

    override suspend fun linkNotificationsToUser(
        email: String,
        userId: UserId,
    ): Result<Int> = runSuspendCatching(TAG) {
        logD(TAG, "Linking notifications for email: $email to user: $userId")

        val updated = postgrest.from(NotificationEntity.COLLECTION).update(
            {
                NotificationEntity::recipientUserId setTo userId.userId
            }
        ) {
            select()
            filter {
                eq("recipient_email", email)
                exact("recipient_user_id", null)
            }
        }.decodeList<NotificationEntity>()

        updated.size
    }

    companion object {
        const val TAG = "SupabaseNotificationDatastore"
    }
}

@SupabaseModel
private fun NotificationEntity.toNotification(): Notification {
    return Notification(
        id = NotificationId(this.id),
        recipientUserId = this.recipientUserId?.let { UserId(it) },
        notificationType = NotificationType.fromString(this.notificationType),
        description = this.description,
        isRead = this.isRead,
        createdAt = this.createdAt,
        readAt = this.readAt,
        inviteId = this.inviteId?.let { InviteId(it) }
    )
}
