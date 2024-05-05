package com.cramsan.edifikana.lib.firestore

enum class EmployeeRole {
    ADMIN,
    SECURITY,
    SECURITY_COVER,
    CLEANING,
    ;
    companion object {
        fun fromString(value: String?): EmployeeRole {
            return when (value) {
                "ADMIN" -> ADMIN
                "SECURITY" -> SECURITY
                "SECURITY_COVER" -> SECURITY_COVER
                "CLEANING" -> CLEANING
                else -> throw IllegalArgumentException("Invalid EmployeeRole value: $value")
            }
        }
    }
}