package com.codehavenx.alpaca.backend.core.storage.requests

import com.codehavenx.alpaca.backend.core.service.models.StaffId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

/**
 * Request model for creating an event.
 */
data class CreateEventRequest(
    val owner: StaffId,
    val attendants: Set<String>,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val timeZone: TimeZone,
)
