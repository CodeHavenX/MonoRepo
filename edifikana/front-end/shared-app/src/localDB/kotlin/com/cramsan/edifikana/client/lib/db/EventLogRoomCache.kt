package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toEntity
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.framework.logging.logI

/**
 * Cache for event logs.
 */
class EventLogRoomCache(
    private val eventLogRecordDao: EventLogRecordDao,
) : EventLogCache {
    /**
     * Get all event log records.
     */
    override suspend fun getRecords(): List<EventLogRecordModel> {
        logI(TAG, "getRecords")

        return eventLogRecordDao.getAll().map { it.toDomainModel() }
    }

    /**
     * Add a new event log record.
     */
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel) {
        logI(TAG, "addRecord")
        eventLogRecordDao.insert(eventLogRecord.toEntity())
    }

    override suspend fun deleteRecord(eventLogRecord: EventLogRecordModel) {
        logI(TAG, "deleteRecord")
        eventLogRecordDao.delete(eventLogRecord.toEntity())
    }

    companion object {
        private const val TAG = "EventLogRoomCache"
    }
}
