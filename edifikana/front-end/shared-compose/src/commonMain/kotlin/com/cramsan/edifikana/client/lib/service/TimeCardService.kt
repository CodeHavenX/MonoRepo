package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK

interface TimeCardService {

    suspend fun getRecords(employeePK: EmployeePK): Result<List<TimeCardRecordModel>>

    suspend fun getAllRecords(): Result<List<TimeCardRecordModel>>

    suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecordModel>

    suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<Unit>
}
