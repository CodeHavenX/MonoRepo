package com.cramsan.edifikana.server.core.service

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
     * Creates an event log with the provided [title].
     */
    suspend fun createEventLog(
        staffId: StaffId?,
        time: Instant,
        title: String,
    ): EventLogEntry {
        return eventLogDatabase.createEventLogEntry(
            request = CreateEventLogEntryRequest(
                staffId = staffId,
                time = time,
                title = title,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves an event log with the provided [id].
     */
    suspend fun getEventLog(
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
    suspend fun getEventLogs(): List<EventLogEntry> {
        val eventLogs = eventLogDatabase.getEventLogEntries().getOrThrow()
        return eventLogs
    }

    /**
     * Updates an event log with the provided [id] and [description].
     */
    suspend fun updateEventLog(
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
    suspend fun deleteEventLog(
        id: EventLogEntryId,
    ): Boolean {
        return eventLogDatabase.deleteEventLogEntry(
            request = DeleteEventLogEntryRequest(
                id = id,
            )
        ).getOrThrow()
    }
}
