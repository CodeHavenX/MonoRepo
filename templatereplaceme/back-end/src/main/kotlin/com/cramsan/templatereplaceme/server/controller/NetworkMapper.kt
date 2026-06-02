package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse
import com.cramsan.templatereplaceme.server.service.models.Pong

/**
 * Maps a [Pong] domain model to a [PongNetworkResponse] network model.
 */
@NetworkModel
fun Pong.toUserNetworkResponse(): PongNetworkResponse {
    return PongNetworkResponse(
        id = id.id,
        firstName = firstName,
        lastName = lastName,
    )
}
