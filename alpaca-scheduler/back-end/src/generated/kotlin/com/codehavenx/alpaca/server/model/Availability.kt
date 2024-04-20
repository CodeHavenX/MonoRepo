package com.codehavenx.alpaca.server.model

/**
 * The availability of a user.
 * @param businessId An Id that identifies a business.
 * @param userId An Id that identifies a user.
 * @param startDate A date with format YYYY-MM-DD.
 * @param endDate A date with format YYYY-MM-DD.
 * @param calendar
 */
data class Availability(
    /* An Id that identifies a business. */
    val businessId: kotlin.String,
    /* An Id that identifies a user. */
    val userId: kotlin.String,
    /* A date with format YYYY-MM-DD. */
    val startDate: java.time.LocalDate,
    /* A date with format YYYY-MM-DD. */
    val endDate: java.time.LocalDate,
    val calendar: kotlin.collections.List<CalendarDay>? = null
)
