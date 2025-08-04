package com.cramsan.edifikana.lib.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a time card event ID.
 */
@Serializable
data class TimeCardEventId(val timeCardEventId: String) {
    override fun toString(): String = timeCardEventId
}
