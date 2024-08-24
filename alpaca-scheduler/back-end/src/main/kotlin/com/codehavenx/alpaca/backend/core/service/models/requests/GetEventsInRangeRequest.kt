package com.codehavenx.alpaca.backend.core.service.models.requests

import com.codehavenx.alpaca.backend.core.service.models.StaffId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

/**
 * Request model for getting events in a range.
 */
data class GetEventsInRangeRequest(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val owners: List<StaffId>,
    val timeZone: TimeZone,
)
