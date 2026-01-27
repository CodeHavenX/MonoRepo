package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.TimeCardApi
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.GetTimeCardEventsQueryParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient

/**
 * Time card service default implementation.
 */
class TimeCardServiceImpl(private val http: HttpClient) : TimeCardService {

    @OptIn(NetworkModel::class)
    override suspend fun getRecords(
        employeePK: EmployeeId,
        propertyId: PropertyId,
    ): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        getRecordsImpl(employeePK, propertyId).getOrThrow()
    }

    @OptIn(NetworkModel::class)
    override suspend fun getAllRecords(propertyId: PropertyId): Result<List<TimeCardRecordModel>> =
        runSuspendCatching(TAG) {
            getRecordsImpl(null, propertyId).getOrThrow()
        }

    // TODO: THIS CURRENTLY PULLS RECORDS FOR ALL PROPERTIES. WE WANT TO UPDATE SO WE ONLY PULL RECORDS FOR SPECIFIED PROPERTIES
    @NetworkModel
    private suspend fun getRecordsImpl(
        employeePK: EmployeeId?,
        propertyId: PropertyId,
    ): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        val response = TimeCardApi
            .getTimeCardEvents
            .buildRequest(GetTimeCardEventsQueryParams(employeePK, propertyId))
            .execute(http)
        val records = response.events.map {
            it.toTimeCardRecordModel()
        }
        records
    }

    @OptIn(NetworkModel::class)
    override suspend fun getRecord(timeCardRecordPK: TimeCardEventId): Result<TimeCardRecordModel> =
        runSuspendCatching(TAG) {
            val response = TimeCardApi
                .getTimeCardEvent
                .buildRequest(timeCardRecordPK)
                .execute(http)
            val record = response.toTimeCardRecordModel()
            record
        }

    @OptIn(NetworkModel::class)
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<TimeCardRecordModel> =
        runSuspendCatching(TAG) {
            val response = TimeCardApi
                .createTimeCardEvent
                .buildRequest(timeCardRecord.toCreateTimeCardEventNetworkRequest())
                .execute(http)

            val record = response.toTimeCardRecordModel()
            record
        }

    companion object {
        private const val TAG = "TimeCardServiceImpl"
    }
}
