package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.server.core.datastore.EventLogDatastore
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import com.cramsan.framework.logging.logD
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Service for event log operations.
 */
@OptIn(ExperimentalTime::class)
class EventLogService(
    private val eventLogDatastore: EventLogDatastore,
) {

    /**
     * Creates an event log entry with the provided parameters.
     */
    suspend fun createEventLogEntry(
        staffId: StaffId?,
        fallbackStaffName: String?,
        propertyId: PropertyId,
        type: EventLogEventType,
        fallbackEventType: String?,
        timestamp: Instant,
        title: String,
        description: String?,
        unit: String,
    ): EventLogEntry {
        logD(TAG, "createEventLogEntry")
        return eventLogDatastore.createEventLogEntry(
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
        logD(TAG, "getEventLogEntry")
        val eventLog = eventLogDatastore.getEventLogEntry(
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
        val eventLogs = eventLogDatastore.getEventLogEntries().getOrThrow()
        return eventLogs
    }

    /**
     * Updates an event log entry with the provided [id] and parameters.
     */
    suspend fun updateEventLogEntry(
        id: EventLogEntryId,
        type: EventLogEventType?,
        fallbackEventType: String?,
        title: String?,
        description: String?,
        unit: String?,
    ): EventLogEntry {
        logD(TAG, "updateEventLogEntry")
        return eventLogDatastore.updateEventLogEntry(
            request = UpdateEventLogEntryRequest(
                id = id,
                type = type,
                fallbackEventType = fallbackEventType,
                title = title,
                description = description,
                unit = unit,
            ),
        ).getOrThrow()
    }

    /**
     * Deletes an event log with the provided [id].
     */
    suspend fun deleteEventLogEntry(
        id: EventLogEntryId,
    ): Boolean {
        logD(TAG, "deleteEventLogEntry")
        return eventLogDatastore.deleteEventLogEntry(
            request = DeleteEventLogEntryRequest(
                id = id,
            )
        ).getOrThrow()
    }

    companion object {
        private const val TAG = "EventLogService"
    }
}
