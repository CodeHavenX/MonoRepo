@file:Suppress("MagicNumber")

package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.server.core.repository.EventLogDatabase
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

/**
 * Dummy implementation of [EventLogDatabase].
 */
class DummyEventLogDatabase(
    private val clock: Clock,
) : EventLogDatabase {
    override suspend fun createEventLogEntry(request: CreateEventLogEntryRequest): Result<EventLogEntry> {
        delay(1000)
        return Result.success(
            EventLogEntry(
                id = EventLogEntryId("1"),
                staffId = StaffId("1"),
                fallbackStaffName = null,
                propertyId = PropertyId("1"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = null,
                timestamp = clock.now(),
                title = "Test",
                description = null,
                unit = "Test"
            )
        )
    }

    override suspend fun getEventLogEntry(request: GetEventLogEntryRequest): Result<EventLogEntry?> {
        delay(1000)
        return Result.success(
            EventLogEntry(
                id = EventLogEntryId("1"),
                staffId = StaffId("1"),
                fallbackStaffName = null,
                propertyId = PropertyId("1"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = null,
                timestamp = clock.now(),
                title = "Test",
                description = null,
                unit = "Test"
            )
        )
    }

    override suspend fun getEventLogEntries(): Result<List<EventLogEntry>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                EventLogEntry(
                    id = EventLogEntryId("1"),
                    staffId = StaffId("1"),
                    fallbackStaffName = null,
                    propertyId = PropertyId("1"),
                    type = EventLogEventType.INCIDENT,
                    fallbackEventType = null,
                    timestamp = clock.now(),
                    title = "Test",
                    description = null,
                    unit = "Test"
                )
            }
        )
    }

    override suspend fun updateEventLogEntry(request: UpdateEventLogEntryRequest): Result<EventLogEntry> {
        delay(1000)
        return Result.success(
            EventLogEntry(
                id = EventLogEntryId("1"),
                staffId = StaffId("1"),
                fallbackStaffName = null,
                propertyId = PropertyId("1"),
                type = EventLogEventType.INCIDENT,
                fallbackEventType = null,
                timestamp = clock.now(),
                title = "Test",
                description = null,
                unit = "Test"
            )
        )
    }

    override suspend fun deleteEventLogEntry(request: DeleteEventLogEntryRequest): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}
