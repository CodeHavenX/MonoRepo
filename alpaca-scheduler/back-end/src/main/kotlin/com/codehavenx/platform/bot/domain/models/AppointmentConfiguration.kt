package com.codehavenx.platform.bot.domain.models

import kotlinx.datetime.TimeZone
import kotlin.time.Duration

data class AppointmentConfiguration(
    val id: String,
    val appointmentType: AppointmentType,
    val duration: Duration,
    val timeZone: TimeZone,
)
