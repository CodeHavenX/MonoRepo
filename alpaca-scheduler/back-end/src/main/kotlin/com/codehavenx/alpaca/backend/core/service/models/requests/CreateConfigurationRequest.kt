package com.codehavenx.alpaca.backend.core.service.models.requests

import com.codehavenx.alpaca.backend.core.service.models.AppointmentType
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

/**
 * Request model for creating an appointment configuration.
 */
data class CreateConfigurationRequest(
    val name: String,
    val appointmentType: AppointmentType,
    val duration: Duration,
    val timeZone: TimeZone,
)
