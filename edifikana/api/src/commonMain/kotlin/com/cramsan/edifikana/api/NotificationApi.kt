package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.network.NotificationListNetworkResponse
import com.cramsan.edifikana.lib.model.network.NotificationNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * Singleton object representing the Notification API with its operations.
 */
@OptIn(NetworkModel::class)
object NotificationApi : Api("notification") {

    /**
     * Get all notifications for the authenticated user.
     */
    val getNotifications = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        NotificationListNetworkResponse,
        >(HttpMethod.Get)

    /**
     * Get a specific notification by ID.
     */
    val getNotification = operation<
        NoRequestBody,
        NoQueryParam,
        NotificationId,
        NotificationNetworkResponse,
        >(HttpMethod.Get)

    /**
     * Mark a notification as read.
     * Uses POST method with path param to avoid routing conflicts.
     */
    val markAsRead = operation<
        NoRequestBody,
        NoQueryParam,
        NotificationId,
        NotificationNetworkResponse,
        >(HttpMethod.Post)

    /**
     * Delete a notification.
     */
    val deleteNotification = operation<
        NoRequestBody,
        NoQueryParam,
        NotificationId,
        NoResponseBody,
        >(HttpMethod.Delete)
}
