package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.EventLogApi
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.GetEventLogEntriesQueryParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient

/**
 * Implementation of [EventLogService] that uses Supabase as the backend.
 */
class EventLogServiceImpl(private val http: HttpClient) : EventLogService {
    @OptIn(NetworkModel::class)
    override suspend fun getRecords(propertyId: PropertyId): Result<List<EventLogRecordModel>> = runSuspendCatching(
        TAG,
    ) {
        val response = EventLogApi.getEventLogEntries
            .buildRequest(
                queryParam = GetEventLogEntriesQueryParams(propertyId),
            )
            .execute(http)

        response.content.map { it.toEventLogRecordModel() }
    }

    @OptIn(NetworkModel::class)
    override suspend fun getRecord(eventLogRecordPK: EventLogEntryId): Result<EventLogRecordModel> =
        runSuspendCatching(TAG) {
            val response = EventLogApi.getEventLogEntry
                .buildRequest(eventLogRecordPK)
                .execute(http)
            val record = response.toEventLogRecordModel()
            record
        }

    @OptIn(NetworkModel::class)
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> =
        runSuspendCatching(TAG) {
            val response = EventLogApi.createEventLogEntry
                .buildRequest(eventLogRecord.toCreateEventLogEntryNetworkRequest())
                .execute(http)
            val record = response.toEventLogRecordModel()
            record
        }

    @OptIn(NetworkModel::class)
    override suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<EventLogRecordModel> =
        runSuspendCatching(TAG) {
            val id = eventLogRecord.id ?: throw IllegalArgumentException(
                "Event log record must have an ID",
            )

            val response = EventLogApi.updateEventLogEntry
                .buildRequest(
                    id,
                    eventLogRecord.toUpdateEventLogEntryNetworkRequest(),
                )
                .execute(http)

            response.toEventLogRecordModel()
        }

    companion object {
        private const val TAG = "EventLogServiceImpl"
    }
}
