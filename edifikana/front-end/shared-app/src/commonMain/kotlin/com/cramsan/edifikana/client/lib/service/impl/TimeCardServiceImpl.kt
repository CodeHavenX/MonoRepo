package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl.Companion.TAG
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.network.TimeCardEventNetworkResponse
import com.cramsan.framework.ammotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Time card service default implementation.
 */
class TimeCardServiceImpl(
    private val http: HttpClient,
) : TimeCardService {

    @OptIn(NetworkModel::class)
    override suspend fun getRecords(
        staffPK: StaffId,
    ): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        getRecordsImpl(staffPK).getOrThrow()
    }

    @OptIn(NetworkModel::class)
    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        getRecordsImpl(null).getOrThrow()
    }

    // TODO: THIS CURRENTLY PULLS RECORDS FOR ALL PROPERTIES. WE WANT TO UPDATE SO WE ONLY PULL RECORDS FOR SPECIFIED PROPERTIES
    @NetworkModel
    private suspend fun getRecordsImpl(
        staffPK: StaffId?,
    ): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.TimeCard.PATH) {
            url {
                staffPK?.let {
                    parameters.append(Routes.TimeCard.QueryParams.STAFF_ID, it.staffId)
                }
            }
        }.body<List<TimeCardEventNetworkResponse>>()
        val records = response.map {
            it.toTimeCardRecordModel()
        }
        records
    }

    @OptIn(NetworkModel::class)
    override suspend fun getRecord(
        timeCardRecordPK: TimeCardEventId,
    ): Result<TimeCardRecordModel> = runSuspendCatching(TAG) {
        val response = http.get("${Routes.TimeCard.PATH}/${timeCardRecordPK.timeCardEventId}")
            .body<TimeCardEventNetworkResponse>()
        val record = response.toTimeCardRecordModel()
        record
    }

    @OptIn(NetworkModel::class)
    override suspend fun addRecord(
        timeCardRecord: TimeCardRecordModel,
    ): Result<TimeCardRecordModel> = runSuspendCatching(TAG) {
        val response = http.post(Routes.TimeCard.PATH) {
            contentType(ContentType.Application.Json)
            setBody(timeCardRecord.toCreateTimeCardEventNetworkRequest())
        }.body<TimeCardEventNetworkResponse>()

        val record = response.toTimeCardRecordModel()
        record
    }
}
