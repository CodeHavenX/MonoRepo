package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.BackgroundDispatcher
import com.cramsan.edifikana.client.android.run
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock

@Singleton
class TimeCardManager @Inject constructor(
    val fireStore: FirebaseFirestore,
    val clock: Clock,
    @BackgroundDispatcher
    val background: CoroutineDispatcher,
) {
    suspend fun getRecords(): Result<List<TimeCardRecord>> = background.run {
        val now = clock.now()

        val twoDaysAgo = now.minus(2.days).epochSeconds

        fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("timeRecorded", Query.Direction.DESCENDING)
            .whereGreaterThan("timeRecorded", twoDaysAgo)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList()
    }

    suspend fun getRecords(employeePK: EmployeePK): Result<List<TimeCardRecord>> = background.run {
        val now = clock.now()

        val twoDaysAgo = now.minus(2.days).epochSeconds

        fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("timeRecorded", Query.Direction.DESCENDING)
            .whereGreaterThan("timeRecorded", twoDaysAgo)
            .whereEqualTo("employeeDocumentId", employeePK.documentPath)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList()
    }

    suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecord> = background.run {
        fireStore.collection(TimeCardRecord.COLLECTION)
            .document(timeCardRecordPK.documentPath)
            .get()
            .await()
            .toObject(TimeCardRecord::class.java) ?: throw Exception("TimeCardRecord not found")
    }

    suspend fun addRecord(timeCardRecord: TimeCardRecord) = background.run {
        val processedRecord = timeCardRecord.copy(
            timeRecorded = clock.now().epochSeconds,
        )
        fireStore.collection(TimeCardRecord.COLLECTION)
            .document(processedRecord.documentId().documentPath)
            .set(processedRecord)
            .await()
    }
}