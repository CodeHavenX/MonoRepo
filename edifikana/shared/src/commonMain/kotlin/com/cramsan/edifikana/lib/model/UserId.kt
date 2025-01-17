package com.cramsan.edifikana.lib.model

/**
 * Domain model representing a user ID.
 */
@JvmInline
value class UserId(val userId: String) {
    override fun toString(): String = userId
}
