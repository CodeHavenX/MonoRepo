package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.NotificationApi
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.network.NotificationListNetworkResponse
import com.cramsan.edifikana.lib.model.network.NotificationNetworkResponse
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.NotificationService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.server.routing.Routing

/**
 * Controller for notification related operations.
 * Handles fetching, marking as read, and deleting notifications for authenticated users.
 */
class NotificationController(
    private val notificationService: NotificationService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    /**
     * Get all notifications for the authenticated user.
     * Returns a list of notifications ordered by creation date (newest first).
     */
    @OptIn(NetworkModel::class)
    suspend fun getNotifications(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
    ): NotificationListNetworkResponse {
        val userId = context.payload.userId

        val notifications = notificationService.getNotificationsForUser(
            userId = userId,
        ).getOrThrow().map { it.toNotificationNetworkResponse() }

        return NotificationListNetworkResponse(notifications)
    }

    /**
     * Get a specific notification by ID.
     * Returns the notification if it belongs to the authenticated user.
     * Throws [ClientRequestExceptions.NotFoundException] if not found.
     * Throws [ClientRequestExceptions.ForbiddenException] if the notification doesn't belong to the user.
     */
    @OptIn(NetworkModel::class)
    suspend fun getNotification(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        notificationId: NotificationId,
    ): NotificationNetworkResponse {
        val userId = context.payload.userId

        val notification = notificationService.getNotification(notificationId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Notification not found: $notificationId")

        // Verify the notification belongs to this user
        if (notification.recipientUserId != userId) {
            throw ClientRequestExceptions.ForbiddenException("Access denied to notification: $notificationId")
        }

        return notification.toNotificationNetworkResponse()
    }

    /**
     * Mark a notification as read.
     * Returns the updated notification.
     * Throws [ClientRequestExceptions.NotFoundException] if not found.
     * Throws [ClientRequestExceptions.ForbiddenException] if the notification doesn't belong to the user.
     */
    @OptIn(NetworkModel::class)
    suspend fun markAsRead(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        notificationId: NotificationId,
    ): NotificationNetworkResponse {
        val userId = context.payload.userId

        // First verify the notification belongs to this user
        val notification = notificationService.getNotification(notificationId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Notification not found: $notificationId")

        if (notification.recipientUserId != userId) {
            throw ClientRequestExceptions.ForbiddenException("Access denied to notification: $notificationId")
        }

        val updatedNotification = notificationService.markAsRead(notificationId).getOrThrow()
        return updatedNotification.toNotificationNetworkResponse()
    }

    /**
     * Delete a notification.
     * Returns [NoResponseBody] to indicate successful deletion.
     * Throws [ClientRequestExceptions.NotFoundException] if not found.
     * Throws [ClientRequestExceptions.ForbiddenException] if the notification doesn't belong to the user.
     */
    @OptIn(NetworkModel::class)
    suspend fun deleteNotification(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        notificationId: NotificationId,
    ): NoResponseBody {
        val userId = context.payload.userId

        // First verify the notification belongs to this user
        val notification = notificationService.getNotification(notificationId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Notification not found: $notificationId")

        if (notification.recipientUserId != userId) {
            throw ClientRequestExceptions.ForbiddenException("Access denied to notification: $notificationId")
        }

        notificationService.deleteNotification(notificationId).getOrThrow()
        return NoResponseBody
    }

    /**
     * Registers the routes for the notification controller.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        NotificationApi.register(route) {
            handler(api.getNotifications, contextRetriever) { request ->
                getNotifications(request.context)
            }
            handler(api.getNotification, contextRetriever) { request ->
                getNotification(request.context, request.pathParam)
            }
            handler(api.markAsRead, contextRetriever) { request ->
                markAsRead(request.context, request.pathParam)
            }
            handler(api.deleteNotification, contextRetriever) { request ->
                deleteNotification(request.context, request.pathParam)
            }
        }
    }
}
