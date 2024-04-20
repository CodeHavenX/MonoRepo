package com.codehavenx.alpaca.server.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Request to set the availability of a user.
 * @param businessId An Id that identifies a business.
 * @param userId An Id that identifies a user.
 * @param startDate A date with format YYYY-MM-DD.
 * @param endDate A date with format YYYY-MM-DD.
 * @param hours * @param daysOfTheWeek */
@Serializable
data class AvailabilityRequest(
    /* An Id that identifies a business. */
    val businessId: kotlin.String,
    /* An Id that identifies a user. */
    val userId: kotlin.String,
    /* A date with format YYYY-MM-DD. */
    val startDate: LocalDate,
    /* A date with format YYYY-MM-DD. */
    val endDate: LocalDate,
    val hours: AvailabilityRequestHours,
    val daysOfTheWeek: kotlin.collections.List<DayOfTheWeek>? = null
)
