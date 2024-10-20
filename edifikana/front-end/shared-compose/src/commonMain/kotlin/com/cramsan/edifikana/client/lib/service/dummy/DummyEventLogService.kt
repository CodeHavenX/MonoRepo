@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import kotlinx.datetime.Clock

/**
 * Dummy implementation of [EventLogService] for testing purposes.
 */
class DummyEventLogService : EventLogService {
    override suspend fun getRecords(): Result<List<EventLogRecordModel>> {
        return Result.success(
            (0..10).map {
                EventLogRecordModel(
                    EventLogEntryId("$it"),
                    "Event $it",
                    StaffId("empoyee_$it"),
                    PropertyId("Property $it"),
                    Clock.System.now().epochSeconds,
                    "Appt $it",
                    EventLogEventType.INCIDENT,
                    null,
                    null,
                    "Note $it",
                    "Description $it",
                    emptyList(),
                )
            }
        )
    }

    override suspend fun getRecord(eventLogRecordPK: EventLogEntryId): Result<EventLogRecordModel> {
        return Result.success(
            EventLogRecordModel(
                eventLogRecordPK,
                "Event ${eventLogRecordPK.eventLogEntryId}",
                StaffId("empoyee_${eventLogRecordPK.eventLogEntryId}"),
                PropertyId("Property Id"),
                Clock.System.now().epochSeconds,
                "Appt 1801",
                EventLogEventType.INCIDENT,
                null,
                null,
                "Note ${eventLogRecordPK.eventLogEntryId}",
                "Description ${eventLogRecordPK.eventLogEntryId}",
                emptyList(),
            )
        )
    }

    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> {
        return TODO()
    }

    override suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> {
        return TODO()
    }
}
