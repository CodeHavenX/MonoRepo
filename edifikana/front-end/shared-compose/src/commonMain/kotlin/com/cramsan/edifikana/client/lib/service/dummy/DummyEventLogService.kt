@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.model.EventLogEventType
import kotlinx.datetime.Clock

/**
 * Dummy implementation of [EventLogService] for testing purposes.
 */
class DummyEventLogService : EventLogService {
    override suspend fun getRecords(): Result<List<EventLogRecordModel>> {
        return Result.success(
            (0..10).map {
                EventLogRecordModel(
                    EventLogRecordPK("$it"),
                    "Event $it",
                    EmployeePK("empoyee_$it"),
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

    override suspend fun getRecord(eventLogRecordPK: EventLogRecordPK): Result<EventLogRecordModel> {
        return Result.success(
            EventLogRecordModel(
                eventLogRecordPK,
                "Event ${eventLogRecordPK.documentPath}",
                EmployeePK("empoyee_${eventLogRecordPK.documentPath}"),
                Clock.System.now().epochSeconds,
                "Appt 1801",
                EventLogEventType.INCIDENT,
                null,
                null,
                "Note ${eventLogRecordPK.documentPath}",
                "Description ${eventLogRecordPK.documentPath}",
                emptyList(),
            )
        )
    }

    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<Unit> {
        return Result.success(Unit)
    }
}
