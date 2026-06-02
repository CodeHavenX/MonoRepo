package com.cramsan.templatereplaceme.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing an ID.
 */
@JvmInline
@Serializable
value class PingPong(val id: String) {
    override fun toString(): String = id
}
