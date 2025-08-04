package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a user ID.
 */
@JvmInline
@Serializable
value class UserId(val userId: String) {
    override fun toString(): String = userId
}
