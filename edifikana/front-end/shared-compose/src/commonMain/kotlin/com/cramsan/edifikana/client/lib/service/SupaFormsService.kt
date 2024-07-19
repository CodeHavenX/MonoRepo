package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.supamappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.Form
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecord
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Duration.Companion.days

class SupaFormsService(
    private val postgrest: Postgrest,
    private val workContext: WorkContext,
) : FormsService {

    @OptIn(FireStoreModel::class)
    override suspend fun getForms(): Result<List<FormModel>> = runSuspendCatching(TAG) {
        postgrest.from(Form.COLLECTION)
            .select {
                filter {
                    eq("propertyId", "cenit_01")
                }
            }
            .decodeList<Form>()
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getForm(formPK: FormPK): Result<FormModel> = runSuspendCatching(TAG) {
        postgrest.from(Form.COLLECTION)
            .select {
                filter {
                    eq("id", formPK.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<Form>()
            .toDomainModel()
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getFormRecords(): Result<List<FormRecordModel>> = runSuspendCatching(TAG) {
        // TODO: Make this range configurable
        val now = workContext.clock.now()
        val fourDaysAgo = now.minus(4.days).epochSeconds

        postgrest.from(FormRecord.COLLECTION)
            .select {
                filter {
                    eq("propertyId", "cenit_01")
                    gt("timeRecorded", fourDaysAgo)
                }
                order("timeRecorded", Order.DESCENDING)
            }
            .decodeList<FormRecord>()
            .map { it.toDomainModel() }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getFormRecord(formRecordPK: FormRecordPK): Result<FormRecordModel> = runSuspendCatching(TAG) {
        postgrest.from(FormRecord.COLLECTION)
            .select {
                filter {
                    eq("id", formRecordPK.documentPath)
                }
                limit(1)
                single()
            }
            .decodeAs<FormRecord>()
            .toDomainModel()
    }

    @OptIn(FireStoreModel::class)
    override suspend fun submitFormRecord(
        formRecordModel: FormRecordModel,
    ): Result<Unit> = runSuspendCatching(TAG) {
        postgrest.from(FormRecord.COLLECTION)
            .insert(formRecordModel.toFirebaseModel("cenit_01"))
    }

    companion object {
        private const val TAG = "SupaFormsService"
    }
}
