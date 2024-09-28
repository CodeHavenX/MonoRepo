package com.cramsan.edifikana.server.core.repository

import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest

/**
 * Interface for interacting with the event log database.
 */
interface EventLogDatabase {

    /**
     * Creates a new event log entry for the given [request]. Returns the [Result] of the operation with the created [EventLogEntry].
     */
    suspend fun createEventLogEntry(
        request: CreateEventLogEntryRequest,
    ): Result<EventLogEntry>

    /**
     * Retrieves an event log entry for the given [request]. Returns the [Result] of the operation with the fetched [EventLogEntry] if found.
     */
    suspend fun getEventLogEntry(
        request: GetEventLogEntryRequest,
    ): Result<EventLogEntry?>

    /**
     * Retrieves all event log entries. Returns the [Result] of the operation with a list of [EventLogEntry].
     */
    suspend fun getEventLogEntries(): Result<List<EventLogEntry>>

    /**
     * Updates an event log entry with the given [request]. Returns the [Result] of the operation with the updated [EventLogEntry].
     */
    suspend fun updateEventLogEntry(
        request: UpdateEventLogEntryRequest,
    ): Result<EventLogEntry>

    /**
     * Deletes an event log entry with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteEventLogEntry(
        request: DeleteEventLogEntryRequest,
    ): Result<Boolean>
}
