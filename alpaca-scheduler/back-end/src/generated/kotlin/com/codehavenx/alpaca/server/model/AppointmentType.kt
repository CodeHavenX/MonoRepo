package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * An appointment type with an Id that identifies it.
 * @param appointmentType
 */
@Serializable
data class AppointmentType(
    val appointmentType: kotlin.String
)
