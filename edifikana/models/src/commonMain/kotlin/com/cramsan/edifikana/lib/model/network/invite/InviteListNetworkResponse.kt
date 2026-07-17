package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for a list of invite.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of invites.")
data class InviteListNetworkResponse(
    @JsonSchema.Description("The invites matching the request.")
    val content: List<InviteNetworkResponse>,
) : ResponseBody
