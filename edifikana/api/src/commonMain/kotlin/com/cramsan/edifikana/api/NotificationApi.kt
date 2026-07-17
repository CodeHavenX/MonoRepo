package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.notification.NotificationListNetworkResponse
import com.cramsan.edifikana.lib.model.network.notification.NotificationNetworkResponse
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * Singleton object representing the Notification API with its operations.
 */

object NotificationApi : Api("notification") {
    /**
     * Get all notifications for the authenticated user.
     */
    val getNotifications =
        operation<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            NotificationListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List notifications",
            description = "Lists all notifications for the authenticated user.",
            responses = UniversalResponsesOnly,
        )

    /**
     * Get a specific notification by ID.
     */
    val getNotification =
        operation<
            NoRequestBody,
            NoQueryParam,
            NotificationId,
            NotificationNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a notification",
            description = "Retrieves a single notification by its identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No notification exists for the given id."
                HttpStatusCode.Forbidden describedAs "The notification belongs to another user."
            },
        )

    /**
     * Mark a notification as read.
     * Uses POST method with path param to avoid routing conflicts.
     */
    val markAsRead =
        operation<
            NoRequestBody,
            NoQueryParam,
            NotificationId,
            NotificationNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Mark a notification as read",
            description = "Marks a notification as read.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No notification exists for the given id."
                HttpStatusCode.Forbidden describedAs "The notification belongs to another user."
            },
        )

    /**
     * Delete a notification.
     */
    val deleteNotification =
        operation<
            NoRequestBody,
            NoQueryParam,
            NotificationId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            summary = "Delete a notification",
            description = "Permanently deletes a notification by its identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No notification exists for the given id."
                HttpStatusCode.Forbidden describedAs "The notification belongs to another user."
            },
        )
}
