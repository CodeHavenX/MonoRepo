package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.network.UserNetworkResponse

/**
 * Maps a [UserNetworkResponse] network model to a [UserModel] domain model.
 */
@OptIn(NetworkModel::class)
fun UserNetworkResponse.toUserModel(): UserModel {
    return UserModel(
        id = UserId(this.id),
        firstName = this.firstName,
        lastName = this.lastName,
    )
}
