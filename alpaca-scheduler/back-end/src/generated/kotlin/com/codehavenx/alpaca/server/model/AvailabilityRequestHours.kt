package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 *
 * @param startTime A time with format HH:mm (24h).
 * @param endTime A time with format HH:mm (24h).
 */
@Serializable
data class AvailabilityRequestHours(
    /* A time with format HH:mm (24h). */
    val startTime: kotlin.String? = null,
    /* A time with format HH:mm (24h). */
    val endTime: kotlin.String? = null
)
