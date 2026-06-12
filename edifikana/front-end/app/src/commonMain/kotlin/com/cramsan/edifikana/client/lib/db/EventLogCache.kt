package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel

/**
 * Event log cache interface.
 */
interface EventLogCache {
    /**
     * Get all event log records.
     */
    suspend fun getRecords(): List<EventLogRecordModel>

    /**
     * Add a new event log record.
     */
    suspend fun addRecord(eventLogRecord: EventLogRecordModel)

    /**
     * Delete an event log record.
     */
    suspend fun deleteRecord(eventLogRecord: EventLogRecordModel)
}
