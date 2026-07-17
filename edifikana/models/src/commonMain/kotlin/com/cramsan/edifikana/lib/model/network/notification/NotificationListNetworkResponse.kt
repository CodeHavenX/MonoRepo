package com.cramsan.edifikana.lib.model.network.notification

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for a list of notifications.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of notifications.")
data class NotificationListNetworkResponse(
    @JsonSchema.Description("The notifications matching the request.")
    val content: List<NotificationNetworkResponse>,
) : ResponseBody
