package com.codehavenx.platform.bot.domain.models

import kotlinx.datetime.DateTimePeriod

data class TimeSlot(
    val staff: UserId,
    val period: DateTimePeriod,
)
