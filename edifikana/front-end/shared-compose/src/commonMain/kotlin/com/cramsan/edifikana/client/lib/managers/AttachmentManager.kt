package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentEntity
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.client.lib.utils.launch
import com.cramsan.edifikana.client.lib.utils.publicDownloadUrl
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manager for attachments.
 */
class AttachmentManager(
    private val eventLogService: EventLogService,
    private val storageService: StorageService,
    private val attachmentDao: FileAttachmentDao,
    private val workContext: WorkContext,
    private val ioDependencies: IODependencies,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    /**
     * Add an attachment to an event log record.
     */
    suspend fun addAttachment(
        fileUris: List<CoreUri>,
        eventLogRecordPK: EventLogEntryId,
    ): Result<Unit> = workContext.getOrCatch(TAG) {
        logI(TAG, "Adding attachment to event log record: $eventLogRecordPK")
        fileUris.forEach { fileUri ->
            val entity = FileAttachmentEntity.create(eventLogRecordPK, workContext.clock, fileUri)
            attachmentDao.insert(entity)
        }
        triggerFullUpload()
    }

    private suspend fun uploadAttachment(attachmentEntity: FileAttachmentEntity) = runCatching {
        mutex.withLock {
            // TODO: Add a domain model layer to avoid having to force-cast nullable fields
            val eventLogRecordPK = EventLogEntryId(requireNotBlank(attachmentEntity.eventLogRecordPK))
            val fileUri = CoreUri.createUri(attachmentEntity.fileUri!!)

            val fileData = readBytes(fileUri, ioDependencies).getOrThrow()

            val fileName = fileUri.getFilename(ioDependencies)

            val uploadRef = fileName

            val imagePhotoResult = storageService.uploadFile(
                fileData,
                uploadRef,
            )

            val imagePhotoRef = imagePhotoResult.getOrThrow()

            val eventLogRecord = eventLogService.getRecord(eventLogRecordPK).getOrThrow()

            val updatedRecord = eventLogRecord.copy(
                attachments = eventLogRecord.attachments + AttachmentHolder(
                    publicUrl = publicDownloadUrl(imagePhotoRef, workContext.storageBucket),
                    storageRef = imagePhotoRef,
                )
            )

            eventLogService.updateRecord(updatedRecord).getOrThrow()
            attachmentDao.delete(attachmentEntity)
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

    /**
     * Start the upload process.
     */
    suspend fun startUpload(): Result<Job> = workContext.getOrCatch(TAG) {
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "AttachmentManager"
    }
}
