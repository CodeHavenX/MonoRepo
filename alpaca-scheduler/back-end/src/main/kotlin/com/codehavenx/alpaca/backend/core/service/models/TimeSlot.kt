package com.codehavenx.alpaca.backend.core.service.models

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a time slot.
 */
data class TimeSlot(
    val staff: StaffId,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)
