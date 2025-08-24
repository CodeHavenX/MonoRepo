package com.cramsan.edifikana.lib.model

/**
 * Enum representing the role of a staff member.
 */
enum class StaffRole {
    MANAGER,
    SECURITY,
    SECURITY_COVER,
    CLEANING,
    ;
    companion object {

        /**
         * Converts a string value to a StaffRole.
         */
        fun fromString(value: String?): StaffRole {
            return when (value) {
                "MANAGER" -> MANAGER
                "SECURITY" -> SECURITY
                "SECURITY_COVER" -> SECURITY_COVER
                "CLEANING" -> CLEANING
                else -> throw IllegalArgumentException("Invalid StaffRole value: $value")
            }
        }
    }
}
