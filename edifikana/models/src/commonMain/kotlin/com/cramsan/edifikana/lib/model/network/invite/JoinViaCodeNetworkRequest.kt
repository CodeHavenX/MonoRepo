package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for joining an organization via an invite code.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to join an organization using an invite code.")
data class JoinViaCodeNetworkRequest(
    @SerialName("invite_code")
    @JsonSchema.Description("Invite code to redeem.")
    val inviteCode: String,
) : RequestBody
