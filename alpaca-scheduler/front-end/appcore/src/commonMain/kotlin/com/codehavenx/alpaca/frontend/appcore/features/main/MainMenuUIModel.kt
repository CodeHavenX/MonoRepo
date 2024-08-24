@file:Suppress("Filename")

package com.codehavenx.alpaca.frontend.appcore.features.main

import com.codehavenx.alpaca.frontend.appcore.models.User

/**
 * UIModel representing a user within the main menu.
 */
data class UserUIModel(
    val username: String,
)

/**
 * Converts a [User] domain model to a [UserUIModel].
 */
fun User.toUIModel(): UserUIModel {
    return UserUIModel(
        username = username,
    )
}
