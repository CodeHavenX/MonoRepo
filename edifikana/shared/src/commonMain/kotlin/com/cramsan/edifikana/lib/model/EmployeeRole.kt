package com.cramsan.edifikana.lib.model

/**
 * Enum representing the role of an employee member.
 */
enum class EmployeeRole {
    MANAGER,
    SECURITY,
    SECURITY_COVER,
    CLEANING,
    ;
    companion object {

        /**
         * Converts a string value to a EmployeeRole.
         */
        fun fromString(value: String?): EmployeeRole {
            return when (value) {
                "MANAGER" -> MANAGER
                "SECURITY" -> SECURITY
                "SECURITY_COVER" -> SECURITY_COVER
                "CLEANING" -> CLEANING
                else -> throw IllegalArgumentException("Invalid EmployeeRole value: $value")
            }
        }
    }
}
