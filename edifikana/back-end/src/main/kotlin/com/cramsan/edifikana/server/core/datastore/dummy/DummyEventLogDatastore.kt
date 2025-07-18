package com.cramsan.edifikana.server.core.datastore.dummy

import com.cramsan.edifikana.server.core.datastore.EventLogDatastore
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import com.cramsan.framework.logging.logD

/**
 * Class with dummy data to be used only for development and testing.
 */
class DummyEventLogDatastore : EventLogDatastore {
    override suspend fun createEventLogEntry(request: CreateEventLogEntryRequest): Result<EventLogEntry> {
        logD(TAG, "createEventLogEntry")
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }

    override suspend fun getEventLogEntry(request: GetEventLogEntryRequest): Result<EventLogEntry?> {
        logD(TAG, "getEventLogEntry")
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }

    override suspend fun getEventLogEntries(): Result<List<EventLogEntry>> {
        logD(TAG, "getEventLogEntries")
        return Result.success(
            listOf(
                EVENT_LOG_ENTRY_STAFF_1_1,
                EVENT_LOG_ENTRY_STAFF_1_2,
                EVENT_LOG_ENTRY_STAFF_2_1,
                EVENT_LOG_ENTRY_STAFF_3_1,
                EVENT_LOG_ENTRY_STAFF_4_1,
            )
        )
    }

    override suspend fun updateEventLogEntry(request: UpdateEventLogEntryRequest): Result<EventLogEntry> {
        logD(TAG, "updateEventLogEntry")
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }

    override suspend fun deleteEventLogEntry(request: DeleteEventLogEntryRequest): Result<Boolean> {
        logD(TAG, "deleteEventLogEntry")
        return Result.success(true)
    }

    companion object {
        private const val TAG = "DummyEventLogDatastore"
    }
}
