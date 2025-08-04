package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Domain model representing a time card event ID.
 */
@JvmInline
@Serializable
value class TimeCardEventId(val timeCardEventId: String) {
    override fun toString(): String = timeCardEventId
}
