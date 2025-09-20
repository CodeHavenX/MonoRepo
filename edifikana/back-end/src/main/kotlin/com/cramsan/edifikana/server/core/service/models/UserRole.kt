package com.cramsan.edifikana.server.core.service.models

import kotlin.IllegalArgumentException

/**
 * Domain model representing a user role. Ordered by increasing privileges.
 */
enum class UserRole {
    UNAUTHORIZED,
    USER,
    EMPLOYEE,
    MANAGER,
    ADMIN,
    OWNER,
    SUPERUSER;
    companion object {

        /**
         * Converts a string value to a UserRole.
         */
        fun fromString(value: String?): UserRole {
            return when (value) {
                "UNAUTHORIZED" -> UNAUTHORIZED
                "USER" -> USER
                "EMPLOYEE" -> EMPLOYEE
                "MANAGER" -> MANAGER
                "ADMIN" -> ADMIN
                "OWNER" -> OWNER
                "SUPERUSER" -> SUPERUSER
                else -> throw IllegalArgumentException("Invalid UserRole value: $value")
            }
        }
    }
}
