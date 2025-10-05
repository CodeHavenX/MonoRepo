package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a list of invite.
 */
@NetworkModel
@Serializable
data class InviteListNetworkResponse(
    val content: List<InviteNetworkResponse>,
) : ResponseBody
