package com.cramsan.edifikana.lib.model

/**
 * Enum representing the role of a staff member.
 */
enum class StaffStatus {
    PENDING,
    ACTIVE,
    ;
    companion object {

        /**
         * Converts a string value to a StaffRole.
         */
        fun fromString(value: String?): StaffStatus {
            return when (value) {
                "PENDING" -> PENDING
                "ACTIVE" -> ACTIVE
                else -> throw IllegalArgumentException("Invalid StaffStatus value: $value")
            }
        }
    }
}
