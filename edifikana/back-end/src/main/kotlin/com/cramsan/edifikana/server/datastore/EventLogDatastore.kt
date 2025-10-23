package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.service.models.EventLogEntry
import kotlin.time.Instant

/**
 * Interface for interacting with the event log database.
 */
interface EventLogDatastore {

    /**
     * Creates a new event log entry for the given [request]. Returns the [Result] of the operation with the created [EventLogEntry].
     */
    suspend fun createEventLogEntry(
        employeeId: EmployeeId?,
        fallbackEmployeeName: String?,
        propertyId: PropertyId,
        type: EventLogEventType,
        fallbackEventType: String?,
        timestamp: Instant,
        title: String,
        description: String?,
        unit: String,
    ): Result<EventLogEntry>

    /**
     * Retrieves an event log entry for the given [request]. Returns the [Result] of the operation with the fetched [EventLogEntry] if found.
     */
    suspend fun getEventLogEntry(
        id: EventLogEntryId,
    ): Result<EventLogEntry?>

    /**
     * Retrieves all event log entries. Returns the [Result] of the operation with a list of [EventLogEntry].
     */
    suspend fun getEventLogEntries(): Result<List<EventLogEntry>>

    /**
     * Updates an event log entry with the given [request]. Returns the [Result] of the operation with the updated [EventLogEntry].
     */
    suspend fun updateEventLogEntry(
        id: EventLogEntryId,
        type: EventLogEventType?,
        fallbackEventType: String?,
        title: String?,
        description: String?,
        unit: String?,
    ): Result<EventLogEntry>

    /**
     * Deletes an event log entry with the given [request]. Returns the [Result] of the operation with a [Boolean] indicating success.
     */
    suspend fun deleteEventLogEntry(
        id: EventLogEntryId,
    ): Result<Boolean>
}
