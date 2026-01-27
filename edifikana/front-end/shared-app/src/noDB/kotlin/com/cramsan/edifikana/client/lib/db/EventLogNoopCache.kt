package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel

/**
 * Noop cache for event logs.
 */
class EventLogNoopCache : EventLogCache {
    /**
     * Get all event log records.
     */
    override suspend fun getRecords(): List<EventLogRecordModel> = emptyList()

    /**
     * Add a new event log record.
     */
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel) = Unit

    override suspend fun deleteRecord(eventLogRecord: EventLogRecordModel) = Unit
}
