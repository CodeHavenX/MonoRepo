package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel

/**
 * Service for managing time card records.
 */
interface TimeCardService {

    /**
     * Get all time card records for a specific staff.
     */
    suspend fun getRecords(staffPK: StaffPK): Result<List<TimeCardRecordModel>>

    /**
     * Get all time card records.
     */
    suspend fun getAllRecords(): Result<List<TimeCardRecordModel>>

    /**
     * Get a specific time card record.
     */
    suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecordModel>

    /**
     * Add a new time card record.
     */
    suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<TimeCardRecordModel>
}
