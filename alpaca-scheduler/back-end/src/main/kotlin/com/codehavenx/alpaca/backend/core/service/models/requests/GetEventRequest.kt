package com.codehavenx.alpaca.backend.core.service.models.requests

import kotlinx.datetime.TimeZone

/**
 * Request model for getting an event.
 */
data class GetEventRequest(
    val eventId: String,
    val timeZone: TimeZone,
)
