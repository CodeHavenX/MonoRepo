package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.EventLogRecordPK

interface EventLogService {
    suspend fun getRecords(): Result<List<EventLogRecordModel>>

    suspend fun getRecord(
        eventLogRecordPK: EventLogRecordPK,
    ): Result<EventLogRecordModel>

    suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<Unit>

    suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<Unit>
}
