package com.codehavenx.alpaca.server.model

/**
 * * @param calendarEventId An Id that identifies a calendar event.
 * @param title * @param startDateTime A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ.
 * @param endDateTime A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ.
 * @param ownerId An Id that identifies a user.
 * @param participantsId */
data class CalendarEvent(
    /* An Id that identifies a calendar event. */
    val calendarEventId: kotlin.String,
    val title: kotlin.String,
    /* A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ. */
    val startDateTime: java.time.OffsetDateTime? = null,
    /* A date and time with format YYYY-MM-DDTHH:mm:ss.SSSZ. */
    val endDateTime: java.time.OffsetDateTime? = null,
    /* An Id that identifies a user. */
    val ownerId: kotlin.String? = null,
    val participantsId: kotlin.collections.List<kotlin.String>? = null
)
