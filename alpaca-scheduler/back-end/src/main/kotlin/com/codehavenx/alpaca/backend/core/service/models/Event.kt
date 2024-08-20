package com.codehavenx.alpaca.backend.core.service.models

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing an event.
 */
data class Event(
    val id: String,
    val owner: StaffId,
    val attendants: Set<UserId>,
    val title: String,
    val description: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)
