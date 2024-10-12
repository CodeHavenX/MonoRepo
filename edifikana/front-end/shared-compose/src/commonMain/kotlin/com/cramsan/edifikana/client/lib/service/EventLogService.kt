package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel

/**
 * Service for managing event logs.
 */
interface EventLogService {

    /**
     * Get all event log records.
     */
    suspend fun getRecords(): Result<List<EventLogRecordModel>>

    /**
     * Get a specific event log record.
     */
    suspend fun getRecord(
        eventLogRecordPK: EventLogRecordPK,
    ): Result<EventLogRecordModel>

    /**
     * Add a new event log record.
     */
    suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel>

    /**
     * Update an existing event log record.
     */
    suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel>
}
