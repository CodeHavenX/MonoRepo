package com.cramsan.flyerboard.lib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the role of a user in the system.
 */
@Serializable
enum class UserRole {
    @SerialName("user")
    USER,

    @SerialName("admin")
    ADMIN,
}
