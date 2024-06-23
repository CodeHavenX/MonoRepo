package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.framework.logging.logI
import com.google.firebase.firestore.Query
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import kotlin.time.Duration.Companion.days

class FirebaseTimeCardService(
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) : TimeCardService {

    @OptIn(FireStoreModel::class)
    override suspend fun getRecords(employeePK: EmployeePK): Result<List<TimeCardRecordModel>> = runSuspendCatching {
        logI(TAG, "getRecords: $employeePK")
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(2.days).epochSeconds

        fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("eventTime", Query.Direction.DESCENDING)
            .where { "eventTime".greaterThan(twoDaysAgo) }
            .where { "employeeDocumentId".equalTo(employeePK.documentPath) }
            .get()
            .documents
            .map { it.data<TimeCardRecord>() }
            .map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = runSuspendCatching {
        logI(TAG, "getAllRecords")
        val now = workContext.clock.now()
        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(3.days).epochSeconds

        fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("eventTime", Query.Direction.DESCENDING)
            .where { "eventTime".greaterThan(twoDaysAgo) }
            .get()
            .documents
            .map { it.data<TimeCardRecord>() }
            .map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getRecord(
        timeCardRecordPK: TimeCardRecordPK,
    ): Result<TimeCardRecordModel> = runSuspendCatching {
        logI(TAG, "getRecord: $timeCardRecordPK")
        val result = fireStore.collection(TimeCardRecord.COLLECTION)
            .document(timeCardRecordPK.documentPath)
            .get()
            .data<TimeCardRecord>()
        result.toDomainModel(workContext.storageBucket)
    }

    @OptIn(FireStoreModel::class)
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<Unit> = runSuspendCatching {
        logI(TAG, "addRecord: $timeCardRecord")
        val firebaseModel = timeCardRecord.toFirebaseModel()
        fireStore.collection(TimeCardRecord.COLLECTION)
            .document(firebaseModel.documentId().documentPath)
            .set(firebaseModel)
    }

    companion object {
        private const val TAG = "FirebaseTimeCardService"
    }
}
