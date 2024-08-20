package com.codehavenx.alpaca.backend.core.storage

import com.codehavenx.alpaca.backend.core.service.models.Event
import com.codehavenx.alpaca.backend.core.storage.requests.CreateEventRequest
import com.codehavenx.alpaca.backend.core.storage.requests.GetEventRequest
import com.codehavenx.alpaca.backend.core.storage.requests.GetEventsInRangeRequest

/**
 * Interface for interacting with the calendar database.
 */
interface CalendarDatabase {
    /**
     * Create an event with the given parameters.
     */
    suspend fun createEvent(
        request: CreateEventRequest,
    ): Result<Event>

    /**
     * Get an event by its ID.
     */
    suspend fun getEvent(
        request: GetEventRequest,
    ): Result<Event?>

    /**
     * Get all events in the given time range.
     */
    suspend fun getEventsInRange(
        request: GetEventsInRangeRequest,
    ): Result<List<Event>>
}
