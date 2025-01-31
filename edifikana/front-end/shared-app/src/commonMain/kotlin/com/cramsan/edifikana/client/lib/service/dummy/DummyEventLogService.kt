package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import io.ktor.client.request.get

/**
 * Dummy implementation of [EventLogService] that returns hardcoded responses.
 */
class DummyEventLogService : EventLogService {

    override suspend fun getRecords(): Result<List<EventLogRecordModel>> {
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

    override suspend fun getRecord(
        eventLogRecordPK: EventLogEntryId,
    ): Result<EventLogRecordModel> {
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }

    override suspend fun addRecord(
        eventLogRecord: EventLogRecordModel,
    ): Result<EventLogRecordModel> {
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }

    override suspend fun updateRecord(
        eventLogRecord: EventLogRecordModel,
    ): Result<EventLogRecordModel> {
        return Result.success(EVENT_LOG_ENTRY_STAFF_1_1)
    }
}
