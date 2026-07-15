package com.cramsan.flyerboard.lib.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the role of a user in the system.
 */
@Serializable
@JsonSchema.Description("Role of a user in the system.")
enum class UserRole {
    @SerialName("user")
    USER,

    @SerialName("admin")
    ADMIN,
}
