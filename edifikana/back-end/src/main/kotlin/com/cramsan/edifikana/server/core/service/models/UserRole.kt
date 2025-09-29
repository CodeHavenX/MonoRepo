package com.cramsan.edifikana.server.core.service.models

import kotlin.IllegalArgumentException

/**
 * Domain model representing a user role. Ordered by decreasing privileges.
 */
@Suppress("MagicNumber")
enum class UserRole(val level: Int) {
    SUPERUSER(0),
    OWNER(10),
    ADMIN(20),
    MANAGER(30),
    EMPLOYEE(40),
    USER(50),
    UNAUTHORIZED(1000);
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
