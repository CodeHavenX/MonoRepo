package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.client.lib.models.PongModel
import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse

/**
 * Maps a [PongNetworkResponse] network model to a [PongModel] domain model.
 */

fun PongNetworkResponse.toPongModel(): PongModel {
    return PongModel(
        id = PingPong(this.id),
        firstName = this.firstName,
        lastName = this.lastName,
    )
}
