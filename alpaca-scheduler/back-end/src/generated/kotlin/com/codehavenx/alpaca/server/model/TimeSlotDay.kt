package com.codehavenx.alpaca.server.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * A single day with the free timeslots.
 * @param date A date with format YYYY-MM-DD.
 * @param events */
@Serializable
data class TimeSlotDay(
    /* A date with format YYYY-MM-DD. */
    val date: LocalDate,
    val events: List<TimeSlot>? = null
)
