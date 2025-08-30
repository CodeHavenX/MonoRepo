package com.cramsan.edifikana.server.core.service.models

import kotlin.IllegalArgumentException

/**
 * Domain model representing a user role.
 */
enum class UserRole {
    SUPERUSER,
    OWNER,
    ADMIN,
    MANAGER,
    EMPLOYEE,
    USER,
    UNAUTHORIZED,
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
                "UNAUTHORIZED" -> UNAUTHORIZED
                else -> throw IllegalArgumentException("Invalid UserRole value: $value")
            }
        }
    }
}
