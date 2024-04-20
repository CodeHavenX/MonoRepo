package com.codehavenx.alpaca.server.model

/**
 * An appointment with all it's respective information.
 * @param businessId An Id that identifies a business.
 * @param userId An Id that identifies a user.
 * @param appointmentConfiguration
 * @param timeSlot
 * @param appointmentId An Id that identifies an appointment.
 */
data class Appointment(
    /* An Id that identifies a business. */
    val businessId: kotlin.String,
    /* An Id that identifies a user. */
    val userId: kotlin.String,
    val appointmentConfiguration: AppointmentConfiguration,
    val timeSlot: TimeSlot,
    /* An Id that identifies an appointment. */
    val appointmentId: kotlin.String? = null
)
