package com.cramsan.edifikana.lib.model.user

import kotlinx.serialization.SerialName
/**
 * Enum representing a user's role in an organization for network transport.
 */
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
        fun fromString(value: String?): UserRole {
            return when (value) {
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
}
