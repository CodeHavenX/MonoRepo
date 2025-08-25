package com.cramsan.edifikana.server.core.service.models

import kotlin.IllegalArgumentException

/**
 * Domain model representing a user role.
 */
enum class UserRole {
    OWNER,
    MANAGER,
    EMPLOYEE,
    USER,
    SUPERUSER,
    UNAUTHORIZED,
    ;
    companion object {

        /**
         * Converts a string value to a UserRole.
         */
        fun fromString(value: String?): UserRole {
            return when (value) {
                "OWNER" -> OWNER
                "MANAGER" -> MANAGER
                "EMPLOYEE" -> EMPLOYEE
                "USER" -> USER
                "SUPERUSER" -> SUPERUSER
                "UNAUTHORIZED" -> UNAUTHORIZED
                else -> throw IllegalArgumentException("Invalid UserRole value: $value")
            }
        }
    }
}
