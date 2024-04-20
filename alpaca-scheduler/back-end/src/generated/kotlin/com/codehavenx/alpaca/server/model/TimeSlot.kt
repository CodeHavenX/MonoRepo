package com.codehavenx.alpaca.server.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * A single slot for availability.
 * @param startTime A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ.
 * @param endTime A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ.
 * @param userId An Id that identifies a user.
 */
@Serializable
data class TimeSlot(
    /* A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ. */
    val startTime: Instant,
    /* A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ. */
    val endTime: Instant,
    /* An Id that identifies a user. */
    val userId: kotlin.String
)
