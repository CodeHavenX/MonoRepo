package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a user ID.
 */
@Serializable
data class UserId(val userId: String) {
    override fun toString(): String = userId
}
