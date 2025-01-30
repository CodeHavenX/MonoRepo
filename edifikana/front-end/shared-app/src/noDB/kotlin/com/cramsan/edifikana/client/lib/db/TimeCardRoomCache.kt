package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.CoreUri

/**
 * Noop cache for time cards.
 */
class TimeCardNoopCache : TimeCardCache {
    /**
     * Get all time card records for a staff member.
     */
    override suspend fun getRecords(staffPK: StaffId): List<TimeCardRecordModel> {
        return emptyList()
    }

    /**
     * Get all time card records.
     */
    override suspend fun getAllRecords(): List<TimeCardRecordModel> {
        return emptyList()
    }

    /**
     * Add a time card record.
     */
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri) {
    }

    override suspend fun deleteRecord(timeCardRecord: TimeCardRecordModel) {
    }

    companion object {
        private const val TAG = "TimeCardNoopCache"
    }
}
