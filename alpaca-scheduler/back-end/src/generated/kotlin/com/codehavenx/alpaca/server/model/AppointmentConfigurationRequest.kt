package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * A request to create or update an appointment configuration.
 * @param appointmentType * @param duration The duration of the appointment in minutes.
 * @param timezone */
@Serializable
data class AppointmentConfigurationRequest(
    val appointmentType: AppointmentType,
    /* The duration of the appointment in minutes. */
    val duration: kotlin.Int,
    val timezone: kotlin.String
)
