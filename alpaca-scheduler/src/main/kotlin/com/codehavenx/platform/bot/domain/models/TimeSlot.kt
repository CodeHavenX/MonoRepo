package com.codehavenx.platform.bot.domain.models

import kotlinx.datetime.LocalDateTime

data class TimeSlot(
    val staff: StaffId,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)
