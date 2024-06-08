package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.days

class FirebaseEventLogService(
    private val fireStore: FirebaseFirestore,
    private val workContext: WorkContext,
) : EventLogService {

    @OptIn(FireStoreModel::class)
    override suspend fun getRecords(): Result<List<EventLogRecordModel>> = runSuspendCatching {
        val now = workContext.clock.now()
        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(4.days).epochSeconds

        fireStore.collection(EventLogRecord.COLLECTION)
            .orderBy("timeRecorded", Query.Direction.DESCENDING)
            .whereGreaterThan("timeRecorded", twoDaysAgo)
            .get()
            .await()
            .toObjects(EventLogRecord::class.java)
            .toList()
            .map { it.toDomainModel(workContext.storageBucket) }
    }

    @OptIn(FireStoreModel::class)
    override suspend fun getRecord(
        eventLogRecordPK: EventLogRecordPK,
    ): Result<EventLogRecordModel> = runSuspendCatching {
        fireStore.collection(EventLogRecord.COLLECTION)
            .document(eventLogRecordPK.documentPath)
            .get()
            .await()
            .toObject(EventLogRecord::class.java)
            ?.toDomainModel(workContext.storageBucket) ?: throw RuntimeException(
            "EventLogRecord $eventLogRecordPK not found"
        )
    }

    @OptIn(FireStoreModel::class)
    override suspend fun addRecord(eventLogRecord: EventLogRecordModel): Result<Unit> = runSuspendCatching {
        val record = eventLogRecord.toFirebaseModel()
        fireStore.collection(EventLogRecord.COLLECTION)
            .document(record.documentId().documentPath)
            .set(record)
            .await()
    }
}
