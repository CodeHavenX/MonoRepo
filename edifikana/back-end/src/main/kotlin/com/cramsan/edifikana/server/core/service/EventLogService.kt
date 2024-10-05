package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.server.core.repository.EventLogDatabase
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.EventLogEntryId
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import kotlinx.datetime.Instant

/**
 * Service for event log operations.
 */
class EventLogService(
    private val eventLogDatabase: EventLogDatabase,
) {

    /**
     * Creates an event log entry with the provided parameters.
     */
    suspend fun createEventLogEntry(
        staffId: StaffId?,
        fallbackStaffName: String?,
        propertyId: String,
        type: EventLogEventType,
        fallbackEventType: String?,
        timestamp: Instant,
        title: String,
        description: String?,
        unit: String,
    ): EventLogEntry {
        return eventLogDatabase.createEventLogEntry(
            request = CreateEventLogEntryRequest(
                staffId = staffId,
                fallbackStaffName = fallbackStaffName,
                propertyId = propertyId,
                type = type,
                fallbackEventType = fallbackEventType,
                timestamp = timestamp,
                title = title,
                description = description,
                unit = unit,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves an event log with the provided [id].
     */
    suspend fun getEventLogEntry(
        id: EventLogEntryId,
    ): EventLogEntry? {
        val eventLog = eventLogDatabase.getEventLogEntry(
            request = GetEventLogEntryRequest(
                id = id,
            ),
        ).getOrNull()

        return eventLog
    }

    /**
     * Retrieves all event logs.
     */
    suspend fun getEventLogEntries(): List<EventLogEntry> {
        val eventLogs = eventLogDatabase.getEventLogEntries().getOrThrow()
        return eventLogs
    }

    /**
     * Updates an event log entry with the provided [id] and parameters.
     */
    suspend fun updateEventLogEntry(
        id: EventLogEntryId,
        staffId: StaffId?,
        time: Instant?,
        title: String,
    ): EventLogEntry {
        return eventLogDatabase.updateEventLogEntry(
            request = UpdateEventLogEntryRequest(
                id = id,
                title = title,
                staffId = staffId,
                time = time,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes an event log with the provided [id].
     */
    suspend fun deleteEventLogEntry(
        id: EventLogEntryId,
    ): Boolean {
        return eventLogDatabase.deleteEventLogEntry(
            request = DeleteEventLogEntryRequest(
                id = id,
            )
        ).getOrThrow()
    }
}
