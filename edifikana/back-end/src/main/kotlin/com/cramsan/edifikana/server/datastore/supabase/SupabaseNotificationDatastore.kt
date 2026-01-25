package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
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

    /**
     * Creates a new notification for a user, linked to an invite.
     * If [recipientUserId] is null, [recipientEmail] is used for later association when user signs up.
     */
    override suspend fun createNotification(
        recipientUserId: UserId?,
        recipientEmail: String?,
        notificationType: NotificationType,
        description: String,
        inviteId: InviteId,
    ): Result<Notification> = runSuspendCatching(TAG) {
        logD(TAG, "Creating notification for user: $recipientUserId, email: $recipientEmail")

        val createEntity = NotificationEntity.Create(
            recipientUserId = recipientUserId?.userId,
            recipientEmail = recipientEmail,
            notificationType = notificationType.name,
            description = description,
            inviteId = inviteId.id,
        )

        val createdEntity = postgrest.from(NotificationEntity.COLLECTION).insert(createEntity) {
            select()
        }.decodeSingle<NotificationEntity>()

        createdEntity.toNotification()
    }

    /**
     * Retrieves a notification by [id]. Returns the [Notification] if found, null otherwise.
     */
    override suspend fun getNotification(
        id: NotificationId,
    ): Result<Notification?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notification: $id")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                NotificationEntity::id eq id.id
                NotificationEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
    }

    /**
     * Retrieves a notification by its associated [inviteId].
     */
    override suspend fun getNotificationByInvite(inviteId: InviteId): Result<Notification?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notification by invite: $inviteId")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                NotificationEntity::inviteId eq inviteId.id
                NotificationEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
    }

    /**
     * Gets notifications for a user, optionally filtering to [unreadOnly].
     */
    override suspend fun getNotificationsForUser(
        userId: UserId,
        unreadOnly: Boolean,
        limit: Int?,
    ): Result<List<Notification>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notifications for user: $userId, unreadOnly: $unreadOnly")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                NotificationEntity::recipientUserId eq userId.userId
                NotificationEntity::deletedAt isExact null
                if (unreadOnly) {
                    NotificationEntity::isRead eq false
                }
            }
            order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit?.let { limit(it.toLong()) }
        }.decodeList<NotificationEntity>().map { it.toNotification() }
    }

    /**
     * Gets notifications sent to an [email] address (before user association).
     */
    override suspend fun getNotificationsByEmail(
        email: String,
    ): Result<List<Notification>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting notifications for email: $email")

        postgrest.from(NotificationEntity.COLLECTION).select {
            filter {
                NotificationEntity::recipientEmail eq email
                NotificationEntity::deletedAt isExact null
            }
            order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
        }.decodeList<NotificationEntity>().map { it.toNotification() }
    }

    /**
     * Marks a notification as read and sets the read timestamp.
     */
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
                NotificationEntity::id eq id.id
            }
        }.decodeSingleOrNull<NotificationEntity>()?.toNotification()
            ?: throw ClientRequestExceptions.NotFoundException("Notification not found: $id")
    }

    /**
     * Soft deletes a notification by [id]. Returns true if successful.
     */
    override suspend fun deleteNotification(
        id: NotificationId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting notification: $id")

        postgrest.from(NotificationEntity.COLLECTION).update({
            NotificationEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                NotificationEntity::id eq id.id
                NotificationEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<NotificationEntity>() != null
    }

    /**
     * Links all notifications for the given email to the specified user.
     * Uses count mode to avoid fetching full notification objects (optimized query).
     */
    override suspend fun linkNotificationsToUser(
        email: String,
        userId: UserId,
    ): Result<Int> = runSuspendCatching(TAG) {
        logD(TAG, "Linking notifications for email: $email to user: $userId")

        // Use count mode to avoid fetching full objects - more efficient
        val result = postgrest.from(NotificationEntity.COLLECTION).update(
            {
                NotificationEntity::recipientUserId setTo userId.userId
            }
        ) {
            filter {
                NotificationEntity::recipientEmail eq email
                NotificationEntity::recipientUserId isExact null
            }
            count(io.github.jan.supabase.postgrest.query.Count.EXACT)
        }

        result.countOrNull()?.toInt() ?: 0
    }

    companion object {
        const val TAG = "SupabaseNotificationDatastore"
    }
}
