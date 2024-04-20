package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * Request to create or update an appointment.
 * @param businessId An Id that identifies a business.
 * @param timeSlot
 * @param appointmentConfigurationId An Id that identifies an appointment configuration.
 */
@Serializable
data class AppointmentRequest(
    /* An Id that identifies a business. */
    val businessId: kotlin.String,
    val timeSlot: TimeSlot,
    /* An Id that identifies an appointment configuration. */
    val appointmentConfigurationId: kotlin.String? = null
)
