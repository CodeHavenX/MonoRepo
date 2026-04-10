package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId

/**
 * Service for managing time card records.
 */
interface TimeCardService {

    /**
     * Get all time card records for a specific employee and [propertyId].
     */
    suspend fun getRecords(employeePK: EmployeeId, propertyId: PropertyId): Result<List<TimeCardRecordModel>>

    /**
     * Get all time card records for the [propertyId].
     */
    suspend fun getAllRecords(propertyId: PropertyId): Result<List<TimeCardRecordModel>>

    /**
     * Get a specific time card record.
     */
    suspend fun getRecord(timeCardRecordPK: TimeCardEventId): Result<TimeCardRecordModel>

    /**
     * Add a new time card record.
     */
    suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<TimeCardRecordModel>
}
