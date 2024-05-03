package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.IdType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock


class EventLogManager @Inject constructor(
    val fireStore: FirebaseFirestore,
    val clock: Clock,
) {
    suspend fun getRecords(): Result<List<EventLogRecord>> = runCatching {
        val now = clock.now()

        val twoDaysAgo = now.minus(2.days).epochSeconds

        fireStore.collection(EventLogRecord.COLLECTION)
            .orderBy("timeRecorded", Query.Direction.DESCENDING)
            .whereGreaterThan("timeRecorded", twoDaysAgo)
            .get()
            .await()
            .toObjects(EventLogRecord::class.java).toList()
    }

    suspend fun getRecord(eventLogRecordPK: EventLogRecordPK): Result<EventLogRecord> = runCatching {
        fireStore.collection(EventLogRecord.COLLECTION)
            .document(eventLogRecordPK.documentPath)
            .get()
            .await()
            .toObject(EventLogRecord::class.java) ?: throw Exception("EventLogRecord not found")
    }

    suspend fun addRecord(eventLogRecord: EventLogRecord) = runCatching {
        fireStore.collection(EventLogRecord.COLLECTION)
            .document(eventLogRecord.documentId().documentPath)
            .set(eventLogRecord)
            .await()
    }
}