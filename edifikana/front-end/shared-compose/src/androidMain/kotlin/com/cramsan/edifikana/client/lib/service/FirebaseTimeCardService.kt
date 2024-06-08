package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.framework.logging.logI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
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
            .whereGreaterThan("eventTime", twoDaysAgo)
            .whereEqualTo("employeeDocumentId", employeePK.documentPath)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList().map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = runSuspendCatching {
        logI(TAG, "getAllRecords")
        val now = workContext.clock.now()
        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(3.days).epochSeconds

        fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("eventTime", Query.Direction.DESCENDING)
            .whereGreaterThan("eventTime", twoDaysAgo)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList().map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getRecord(
        timeCardRecordPK: TimeCardRecordPK,
    ): Result<TimeCardRecordModel> = runSuspendCatching {
        logI(TAG, "getRecord: $timeCardRecordPK")
        val result = fireStore.collection(TimeCardRecord.COLLECTION)
            .document(timeCardRecordPK.documentPath)
            .get()
            .await()
            .toObject(TimeCardRecord::class.java) ?: throw RuntimeException(
            "TimeCardRecord $timeCardRecordPK not found"
        )
        result.toDomainModel(workContext.storageBucket)
    }

    @OptIn(FireStoreModel::class)
    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<Unit> = runSuspendCatching {
        logI(TAG, "addRecord: $timeCardRecord")
        val firebaseModel = timeCardRecord.toFirebaseModel()
        fireStore.collection(TimeCardRecord.COLLECTION)
            .document(firebaseModel.documentId().documentPath)
            .set(firebaseModel)
            .await()
    }

    companion object {
        private const val TAG = "FirebaseTimeCardService"
    }
}
