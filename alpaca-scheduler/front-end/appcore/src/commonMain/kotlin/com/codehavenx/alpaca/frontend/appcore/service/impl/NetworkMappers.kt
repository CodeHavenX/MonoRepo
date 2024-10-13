package com.codehavenx.alpaca.frontend.appcore.service.impl

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.UserResponse

/**
 * Convert a user response to a user model.
 */
@NetworkModel
fun UserResponse.toModel(): User {
    return User(
        id = id,
        username = username,
    )
}
