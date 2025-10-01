package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.Serializable

/**
 * Response model for an invite.
 */
@NetworkModel
@Serializable
data class InviteNetworkResponse(
    val inviteId: InviteId,
    val email: String,
)
