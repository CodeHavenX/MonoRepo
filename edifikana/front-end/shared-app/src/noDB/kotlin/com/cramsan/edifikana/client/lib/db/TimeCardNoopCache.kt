package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.CoreUri

/**
 * Noop cache for time cards.
 */
class TimeCardNoopCache : TimeCardCache {
    /**
     * Get all time card records for an employee member.
     */
    override suspend fun getRecords(employeePK: EmployeeId): List<TimeCardRecordModel> = emptyList()

    /**
     * Get all time card records.
     */
    override suspend fun getAllRecords(): List<TimeCardRecordModel> = emptyList()

    /**
     * Add a time card record.
     */
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri) = Unit

    override suspend fun deleteRecord(timeCardRecord: TimeCardRecordModel) = Unit
}
