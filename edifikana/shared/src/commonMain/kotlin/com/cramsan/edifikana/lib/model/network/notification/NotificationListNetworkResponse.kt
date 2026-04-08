package com.cramsan.edifikana.lib.model.network.notification

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a list of notifications.
 */
@NetworkModel
@Serializable
data class NotificationListNetworkResponse(
    val content: List<NotificationNetworkResponse>,
) : ResponseBody
