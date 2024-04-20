package com.codehavenx.alpaca.server.model

/**
 * An appointment configuration with all it's respective information.
 * @param appointmentConfigurationId An Id that identifies an appointment configuration.
 * @param appointmentType * @param duration The duration of the appointment in minutes.
 * @param timezone */
data class AppointmentConfiguration(
    /* An Id that identifies an appointment configuration. */
    val appointmentConfigurationId: kotlin.String,
    val appointmentType: AppointmentType,
    /* The duration of the appointment in minutes. */
    val duration: kotlin.Int,
    val timezone: kotlin.String
)
