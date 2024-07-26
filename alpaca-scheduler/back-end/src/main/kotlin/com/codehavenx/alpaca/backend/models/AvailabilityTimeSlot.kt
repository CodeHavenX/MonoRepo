package com.codehavenx.alpaca.backend.models

import kotlinx.datetime.DateTimePeriod

data class AvailabilityTimeSlot(
    val options: List<UserId>,
    val period: DateTimePeriod,
)
