package com.codehavenx.alpaca.backend.core.service.models

import kotlinx.datetime.TimeZone
import kotlin.time.Duration

/**
 * Domain model representing an appointment configuration.
 */
data class AppointmentConfiguration(
    val id: String,
    val appointmentType: AppointmentType,
    val duration: Duration,
    val timeZone: TimeZone,
)
