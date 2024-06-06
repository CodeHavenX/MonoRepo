package com.cramsan.edifikana.client.android.managers

import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.client.android.utils.launch
import com.cramsan.edifikana.client.lib.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toEntity
import com.cramsan.edifikana.client.lib.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.framework.logging.logE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class EventLogManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val eventLogRecordDao: EventLogRecordDao,
    private val attachmentDao: FileAttachmentDao,
    private val workContext: WorkContext,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    @OptIn(FireStoreModel::class)
    suspend fun getRecords(): Result<List<EventLogRecordModel>> = workContext.getOrCatch(TAG) {
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(4.days).epochSeconds

        val cachedData = eventLogRecordDao.getAll().map { it.toDomainModel() }

        val onlineData = fireStore.collection(EventLogRecord.COLLECTION)
            .orderBy("timeRecorded", Query.Direction.DESCENDING)
            .whereGreaterThan("timeRecorded", twoDaysAgo)
            .get()
            .await()
            .toObjects(EventLogRecord::class.java)
            .toList()
            .map { it.toDomainModel(workContext.storageBucket) }

        (cachedData + onlineData).sortedByDescending { it.timeRecorded }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getRecord(
        eventLogRecordPK: EventLogRecordPK,
    ): Result<EventLogRecordModel> = workContext.getOrCatch(TAG) {
        val localAttachments = attachmentDao.getAll()
            .filter { it.eventLogRecordPK == eventLogRecordPK.documentPath }
            .mapNotNull { it.fileUri?.let { uri -> AttachmentHolder(publicUrl = uri, storageRef = null) } }
        val record = fireStore.collection(EventLogRecord.COLLECTION)
            .document(eventLogRecordPK.documentPath)
            .get()
            .await()
            .toObject(EventLogRecord::class.java)
            ?.toDomainModel(workContext.storageBucket) ?: throw RuntimeException(
            "EventLogRecord $eventLogRecordPK not found"
        )
        record.copy(
            attachments = localAttachments + record.attachments,
        )
    }

    suspend fun addRecord(eventLogRecord: EventLogRecordModel) = workContext.getOrCatch(TAG) {
        eventLogRecordDao.insert(eventLogRecord.toEntity())

        workContext.launch(TAG) {
            uploadRecord(eventLogRecord)
            triggerFullUpload()
        }
        Unit
    }

    @OptIn(FireStoreModel::class)
    private suspend fun uploadRecord(eventLogRecord: EventLogRecordModel) = runCatching {
        mutex.withLock {
            val record = eventLogRecord.toFirebaseModel()
            fireStore.collection(EventLogRecord.COLLECTION)
                .document(record.documentId().documentPath)
                .set(record)
                .await()

            eventLogRecordDao.delete(eventLogRecord.toEntity())
        }
    }.onFailure { logE(TAG, "Failed to upload event record", it) }

    private suspend fun triggerFullUpload(): Job {
        uploadJob?.cancel()
        return workContext.launch(TAG) {
            val pending = eventLogRecordDao.getAll()

            pending.forEach { record ->
                uploadRecord(record.toDomainModel())
            }
        }.also {
            uploadJob = it
        }
    }

    suspend fun startUpload(): Result<Job> = workContext.getOrCatch(TAG) {
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "EventLogManager"
    }
}
