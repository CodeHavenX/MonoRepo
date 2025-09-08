package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.Serializable

@NetworkModel
@Serializable
data class InviteNetworkResponse(
    val inviteId: String,
    val email: String,
)
