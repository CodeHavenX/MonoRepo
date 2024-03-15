package com.codehavenx.platform.bot.domain.models

import kotlinx.datetime.DateTimePeriod

data class AvailabilityTimeSlot(
    val options: List<UserId>,
    val period: DateTimePeriod,
)
