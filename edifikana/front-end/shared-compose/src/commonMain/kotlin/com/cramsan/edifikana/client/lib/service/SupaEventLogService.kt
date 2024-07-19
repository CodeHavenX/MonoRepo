package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.supa.EventLogRecord
import com.cramsan.edifikana.lib.supa.SupabaseModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Duration.Companion.days

class SupaEventLogService(
    private val postgrest: Postgrest,
    private val workContext: WorkContext,
) : EventLogService {

    @OptIn(SupabaseModel::class)
    override suspend fun getRecords(): Result<List<EventLogRecordModel>> = runSuspendCatching(TAG) {
        val now = workContext.clock.now()
        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(4.days).epochSeconds

        postgrest.from(EventLogRecord.COLLECTION)
            .select() {
                filter {
                    gt("timeRecorded", twoDaysAgo)
                }
                order("timeRecorded", Order.DESCENDING)
            }
            .decodeList<EventLogRecord>()
            .map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getRecord(
        eventLogRecordPK: EventLogRecordPK,
    ): Result<EventLogRecordModel> = runSuspendCatching(TAG) {
        postgrest.from(EventLogRecord.COLLECTION)
            .select {
                filter {
                    eq("pk", eventLogRecordPK.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<EventLogRecord>()
            .toDomainModel(workContext.storageBucket)
    }

    @OptIn(SupabaseModel::class)
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<Unit> = runSuspendCatching(TAG) {
        postgrest.from(EventLogRecord.COLLECTION)
            .insert(eventLogRecord.toFirebaseModel())
    }

    @OptIn(SupabaseModel::class)
    override suspend fun updateRecord(eventLogRecord: EventLogRecordModel): Result<Unit> = runSuspendCatching(TAG) {
        postgrest.from(EventLogRecord.COLLECTION)
            .update(eventLogRecord.toFirebaseModel())
    }

    companion object {
        private const val TAG = "SupaEventLogService"
    }
}
