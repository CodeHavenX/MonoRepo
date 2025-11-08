package com.cramsan.templatereplaceme.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a user ID.
 */
@JvmInline
@Serializable
value class UserId(val userId: String) : PathParam {
    override fun toString(): String = userId
}
