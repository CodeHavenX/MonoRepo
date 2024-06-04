package com.cramsan.edifikana.client.android.managers

import android.net.Uri
import com.cramsan.edifikana.client.android.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.android.db.models.FileAttachmentEntity
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.utils.getFilename
import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.client.android.utils.launch
import com.cramsan.edifikana.lib.firestore.EventLogRecord
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.requireNotBlank
import com.cramsan.edifikana.lib.storage.FOLDER_ATTACHMENTS
import com.cramsan.framework.logging.logE
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttachmentManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storageService: StorageService,
    private val attachmentDao: FileAttachmentDao,
    private val workContext: WorkContext,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    suspend fun addAttachment(
        fileUris: List<Uri>,
        eventLogRecordPK: EventLogRecordPK,
    ): Result<Unit> = workContext.getOrCatch(TAG) {
        fileUris.forEach { fileUri ->
            val entity = FileAttachmentEntity.create(eventLogRecordPK, workContext.clock, fileUri)
            attachmentDao.insert(entity)
        }
        triggerFullUpload()
    }

    @OptIn(FireStoreModel::class)
    private suspend fun uploadAttachment(attachmentEntity: FileAttachmentEntity) = runCatching {
        mutex.withLock {
            // TODO: Add a domain model layer to avoid having to force-cast nullable fields
            val eventLogRecordPK = requireNotBlank(attachmentEntity.eventLogRecordPK)
            val fileUri = Uri.parse(attachmentEntity.fileUri)
            workContext.appContext.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val fileData = inputStream.readBytes()

                val fileName = fileUri.getFilename(workContext.appContext.contentResolver)
                val uploadPath = listOf(FOLDER_ATTACHMENTS)

                val uploadRef = StorageRef(
                    fileName,
                    uploadPath,
                )
                val imagePhotoResult = storageService.uploadFile(
                    fileData,
                    uploadRef,
                )

                val imagePhotoRef = imagePhotoResult.getOrThrow()

                val eventLogRecord = fireStore.collection(EventLogRecord.COLLECTION)
                    .document(eventLogRecordPK)
                    .get()
                    .await()
                    .toObject(EventLogRecord::class.java) ?: throw RuntimeException("EventLogRecord not found")

                val updatedRecord = eventLogRecord.copy(
                    attachments = (eventLogRecord.attachments.orEmpty()) + imagePhotoRef.ref,
                )

                fireStore.collection(EventLogRecord.COLLECTION)
                    .document(eventLogRecordPK)
                    .set(updatedRecord)
                    .await()

                attachmentDao.delete(attachmentEntity)
            }
        }
    }.onFailure { logE(TAG, "Failed to upload attachment", it) }

    private suspend fun triggerFullUpload(): Job {
        uploadJob?.cancel()
        return workContext.launch(TAG) {
            val pending = attachmentDao.getAll()

            pending.forEach { record ->
                uploadAttachment(record)
            }
        }.also {
            uploadJob = it
        }
    }

    companion object {
        private const val TAG = "AttachmentManager"
    }
}
