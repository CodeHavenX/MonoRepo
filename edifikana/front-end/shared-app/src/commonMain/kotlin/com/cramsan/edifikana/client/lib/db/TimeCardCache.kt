package com.cramsan.edifikana.client.lib.db

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.CoreUri

/**
 * Time card cache interface.
 */
interface TimeCardCache {
    /**
     * Get all time card records for an employee member.
     */
    suspend fun getRecords(employeePK: EmployeeId): List<TimeCardRecordModel>

    /**
     * Get all time card records.
     */
    suspend fun getAllRecords(): List<TimeCardRecordModel>

    /**
     * Add a time card record.
     */
    suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri)

    /**
     * Delete a time card record.
     */
    suspend fun deleteRecord(timeCardRecord: TimeCardRecordModel)
}
