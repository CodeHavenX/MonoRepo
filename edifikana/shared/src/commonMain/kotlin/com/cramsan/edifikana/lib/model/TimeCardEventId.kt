package com.cramsan.edifikana.lib.model

import kotlin.jvm.JvmInline

/**
 * Domain model representing a time card event ID.
 */
@JvmInline
value class TimeCardEventId(val timeCardEventId: String) {
    override fun toString(): String = timeCardEventId
}
