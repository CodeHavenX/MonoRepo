package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.templatereplaceme.lib.model.network.UserNetworkResponse
import com.cramsan.templatereplaceme.server.service.models.User

/**
 * Maps a [User] domain model to a [UserNetworkResponse] network model.
 */
@NetworkModel
fun User.toUserNetworkResponse(): UserNetworkResponse = UserNetworkResponse(
    id = id.userId,
    firstName = firstName,
    lastName = lastName,
)
