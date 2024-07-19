package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.supamappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.supa.SupabaseModel
import com.cramsan.edifikana.lib.supa.TimeCardRecord
import com.cramsan.framework.logging.logI
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.days

class SupaTimeCardService(
    private val postgrest: Postgrest,
    private val workContext: WorkContext,
    private val storage: Storage,
) : TimeCardService {

    @OptIn(SupabaseModel::class)
    override suspend fun getRecords(
        employeePK: EmployeePK,
    ): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        logI(TAG, "getRecords: $employeePK")
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(2.days).epochSeconds

        postgrest.from(com.cramsan.edifikana.lib.supa.TimeCardRecord.COLLECTION)
            .select {
                filter {
                    eq("employeeDocumentId", employeePK.documentPath)
                    gt("eventTime", twoDaysAgo)
                }
                order("eventTime", Order.DESCENDING)
            }
            .decodeList<TimeCardRecord>()
            .map { it.toDomainModel(storage) }
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = runSuspendCatching(TAG) {
        logI(TAG, "getAllRecords")
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(3.days).epochSeconds

        postgrest.from(TimeCardRecord.COLLECTION)
            .select {
                filter {
                    gt("eventTime", twoDaysAgo)
                }
                order("eventTime", Order.DESCENDING)
            }
            .decodeList<TimeCardRecord>()
            .map { it.toDomainModel(storage) }
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getRecord(
        timeCardRecordPK: TimeCardRecordPK,
    ): Result<TimeCardRecordModel> = runSuspendCatching(TAG) {
        logI(TAG, "getRecord: $timeCardRecordPK")
        postgrest.from(TimeCardRecord.COLLECTION)
            .select {
                filter {
                    eq("pk", timeCardRecordPK.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<TimeCardRecord>()
            .toDomainModel(storage)
    }

    @OptIn(SupabaseModel::class)
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<Unit> = runSuspendCatching(TAG) {
        logI(TAG, "addRecord: $timeCardRecord")
        postgrest.from(TimeCardRecord.COLLECTION)
            .insert(timeCardRecord.toFirebaseModel())
    }

    companion object {
        private const val TAG = "SupaTimeCardService"
    }
}
