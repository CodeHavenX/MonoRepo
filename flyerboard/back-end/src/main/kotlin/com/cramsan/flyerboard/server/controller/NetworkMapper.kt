package com.cramsan.flyerboard.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.flyerboard.lib.model.network.UserNetworkResponse
import com.cramsan.flyerboard.server.service.models.User

/**
 * Maps a [User] domain model to a [UserNetworkResponse] network model.
 */
@NetworkModel
fun User.toUserNetworkResponse(): UserNetworkResponse {
    return UserNetworkResponse(
        id = id.userId,
        firstName = firstName,
        lastName = lastName,
    )
}
