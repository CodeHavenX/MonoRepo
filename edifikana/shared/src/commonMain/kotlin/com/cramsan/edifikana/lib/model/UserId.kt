package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a user ID.
 */
@JvmInline
value class UserId(val userId: String) {
    override fun toString(): String = userId
}
