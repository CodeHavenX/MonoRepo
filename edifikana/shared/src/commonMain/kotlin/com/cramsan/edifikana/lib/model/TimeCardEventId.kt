package com.cramsan.edifikana.lib.model

import com.cramsan.framework.annotations.api.PathParam
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a time card event ID.
 */
@JvmInline
@Serializable
value class TimeCardEventId(val timeCardEventId: String) : PathParam {
    override fun toString(): String = timeCardEventId
}
