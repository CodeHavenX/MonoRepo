package com.cramsan.edifikana.client.lib.service.supabase

import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [EventLogService] that uses Supabase as the backend.
 */
class SupabaseEventLogService(
    private val http: HttpClient,
) : EventLogService {
    @OptIn(NetworkModel::class)
    override suspend fun getRecords(): Result<List<EventLogRecordModel>> {
        val response = http.get(Routes.EventLog.PATH).body<List<EventLogEntryNetworkResponse>>()
        val records = response.map {
            it.toEventLogRecordModel()
        }
        return Result.success(records)
    }

    @OptIn(NetworkModel::class)
    override suspend fun getRecord(eventLogRecordPK: EventLogRecordPK): Result<EventLogRecordModel> {
        val response = http.get(
            "${Routes.EventLog.PATH}/${eventLogRecordPK.documentPath}"
        ).body<EventLogEntryNetworkResponse>()
        val record = response.toEventLogRecordModel()
        return Result.success(record)
    }

    @OptIn(NetworkModel::class)
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> {
        val response = http.post(Routes.EventLog.PATH) {
            contentType(ContentType.Application.Json)
            setBody(eventLogRecord.toCreateEventLogEntryNetworkRequest())
        }.body<EventLogEntryNetworkResponse>()
        val record = response.toEventLogRecordModel()
        return Result.success(record)
    }

    @OptIn(NetworkModel::class)
    override suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> {
        val id = eventLogRecord.id?.documentPath ?: throw IllegalArgumentException("Event log record must have an ID")
        val response = http.put("${Routes.EventLog.PATH}/$id") {
            contentType(ContentType.Application.Json)
            setBody(eventLogRecord.toUpdateEventLogEntryNetworkRequest())
        }.body<EventLogEntryNetworkResponse>()
        val record = response.toEventLogRecordModel()
        return Result.success(record)
    }
}
