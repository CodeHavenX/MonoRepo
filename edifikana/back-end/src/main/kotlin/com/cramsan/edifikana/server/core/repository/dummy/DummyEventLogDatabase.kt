package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.EventLogDatabase
import com.cramsan.edifikana.server.core.service.models.EventLogEntry
import com.cramsan.edifikana.server.core.service.models.EventLogEntryId
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class DummyEventLogDatabase(
    private val clock: Clock,
) : EventLogDatabase {
    override suspend fun createEventLogEntry(request: CreateEventLogEntryRequest): Result<EventLogEntry> {
        delay(1000)
        return Result.success(
            EventLogEntry(
                id = EventLogEntryId("1"),
                staffId = StaffId("1"),
                time = clock.now(),
                title = "Test"
            )
        )
    }

    override suspend fun getEventLogEntry(request: GetEventLogEntryRequest): Result<EventLogEntry?> {
        delay(1000)
        return Result.success(
            EventLogEntry(
                id = EventLogEntryId("1"),
                staffId = StaffId("1"),
                time = clock.now(),
                title = "Test"
            )
        )
    }

    override suspend fun getEventLogEntries(): Result<List<EventLogEntry>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                EventLogEntry(
                    id = EventLogEntryId(it.toString()),
                    staffId = StaffId(it.toString()),
                    time = clock.now(),
                    title = "Test $it"
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
                time = clock.now(),
                title = "Test"
            )
        )
    }

    override suspend fun deleteEventLogEntry(request: DeleteEventLogEntryRequest): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}