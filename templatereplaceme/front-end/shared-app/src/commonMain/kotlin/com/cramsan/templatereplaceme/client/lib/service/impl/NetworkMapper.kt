package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.client.lib.models.UserModel
import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse

/**
 * Maps a [PongNetworkResponse] network model to a [UserModel] domain model.
 */

fun PongNetworkResponse.toUserModel(): UserModel {
    return UserModel(
        id = PingPong(this.id),
        firstName = this.firstName,
        lastName = this.lastName,
    )
}
