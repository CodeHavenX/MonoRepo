@file:OptIn(NetworkModel::class)

package com.codehavenx.alpaca.frontend.appcore.service.impl.mappers

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.UserResponse

/**
 * Map the user response to the user domain model.
 */
@NetworkModel
fun UserResponse.toModel(): User {
    return User(
        id = id,
        username = username,
    )
}
