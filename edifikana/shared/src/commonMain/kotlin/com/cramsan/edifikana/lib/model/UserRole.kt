package com.cramsan.edifikana.lib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum representing a user's role in an organization for network transport.
 */
@Serializable
enum class UserRole {
    @SerialName("SUPERUSER")
    SUPERUSER,

    @SerialName("OWNER")
    OWNER,

    @SerialName("ADMIN")
    ADMIN,

    @SerialName("MANAGER")
    MANAGER,

    @SerialName("EMPLOYEE")
    EMPLOYEE,

    @SerialName("USER")
    USER,
    ;

    companion object {
        /**
         * Converts a string value to a UserRole.
         */
        fun fromString(value: String?): UserRole = when (value) {
            "SUPERUSER" -> SUPERUSER
            "OWNER" -> OWNER
            "ADMIN" -> ADMIN
            "MANAGER" -> MANAGER
            "EMPLOYEE" -> EMPLOYEE
            "USER" -> USER
            else -> throw IllegalArgumentException("Invalid UserRole value: $value")
        }
    }
}
