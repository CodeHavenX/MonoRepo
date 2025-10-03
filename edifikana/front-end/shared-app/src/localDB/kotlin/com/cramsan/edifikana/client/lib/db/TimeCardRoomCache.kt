package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toEntity
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logI

/**
 * Caching for time cards.
 */
class TimeCardRoomCache(
    private val timeCardRecordDao: TimeCardRecordDao,
) : TimeCardCache {
    /**
     * Get all time card records for a employee member.
     */
    override suspend fun getRecords(employeePK: EmployeeId): List<TimeCardRecordModel> {
        logI(TAG, "getRecords")
        return timeCardRecordDao.getAll(employeePK.empId).map { it.toDomainModel() }
    }

    /**
     * Get all time card records.
     */
    override suspend fun getAllRecords(): List<TimeCardRecordModel> {
        logI(TAG, "getAllRecords")
        return timeCardRecordDao.getAll().map { it.toDomainModel() }
    }

    /**
     * Add a time card record.
     */
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri) {
        logI(TAG, "addRecord")
        val entity = timeCardRecord.toEntity(cachedImageUrl)
        timeCardRecordDao.insert(entity)
    }

    override suspend fun deleteRecord(timeCardRecord: TimeCardRecordModel) {
        logI(TAG, "deleteRecord")
        val entity = timeCardRecord.toEntity(CoreUri.createUri(""))
        timeCardRecordDao.delete(entity)
    }

    companion object {
        private const val TAG = "TimeCardCache"
    }
}
