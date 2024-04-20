package com.codehavenx.alpaca.server.model

/**
 * A single day with all its events.
 * @param date A date with format YYYY-MM-DD.
 * @param events * @param startTime A time with format HH:mm (24h).
 * @param endTime A time with format HH:mm (24h).
 */
data class CalendarDay(
    /* A date with format YYYY-MM-DD. */
    val date: java.time.LocalDate,
    val events: kotlin.collections.List<CalendarEvent>,
    /* A time with format HH:mm (24h). */
    val startTime: kotlin.String? = null,
    /* A time with format HH:mm (24h). */
    val endTime: kotlin.String? = null
)
