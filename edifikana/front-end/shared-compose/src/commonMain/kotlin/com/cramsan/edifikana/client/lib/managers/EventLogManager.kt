package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toEntity
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.client.lib.utils.launch
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manager for event logs.
 */
class EventLogManager(
    private val eventLogService: EventLogService,
    private val eventLogRecordDao: EventLogRecordDao,
    private val attachmentDao: FileAttachmentDao,
    private val workContext: WorkContext,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    /**
     * Get all event log records.
     */
    suspend fun getRecords(): Result<List<EventLogRecordModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getRecords")

        val cachedData = eventLogRecordDao.getAll().map { it.toDomainModel() }

        val onlineData = eventLogService.getRecords()
            .getOrThrow()

        (cachedData + onlineData).sortedByDescending { it.timeRecorded }
    }

    /**
     * Get a specific event log record.
     */
    suspend fun getRecord(
        eventLogRecordPK: EventLogEntryId,
    ): Result<EventLogRecordModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getRecord")
        val localAttachments = attachmentDao.getAll()
            .filter { it.eventLogRecordPK == eventLogRecordPK.eventLogEntryId }
            .mapNotNull { it.fileUri?.let { uri -> AttachmentHolder(publicUrl = uri, storageRef = null) } }
        val record = eventLogService.getRecord(eventLogRecordPK).getOrThrow()
        record.copy(
            attachments = localAttachments + record.attachments,
        )
    }

    /**
     * Add a new event log record.
     */
    suspend fun addRecord(eventLogRecord: EventLogRecordModel) = workContext.getOrCatch(TAG) {
        logI(TAG, "addRecord")
        eventLogRecordDao.insert(eventLogRecord.toEntity())

        workContext.launch(TAG) {
            uploadRecord(eventLogRecord)
            triggerFullUpload()
        }
        Unit
    }

    private suspend fun uploadRecord(eventLogRecord: EventLogRecordModel) = runCatching {
        logI(TAG, "uploadRecord")
        mutex.withLock {
            eventLogService.addRecord(eventLogRecord).getOrThrow()

            eventLogRecordDao.delete(eventLogRecord.toEntity())
        }
    }.onFailure { logE(TAG, "Failed to upload event record", it) }

    private suspend fun triggerFullUpload(): Job {
        logI(TAG, "triggerFullUpload")
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

    /**
     * Start the upload process.
     */
    suspend fun startUpload(): Result<Job> = workContext.getOrCatch(TAG) {
        logI(TAG, "startUpload")
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "EventLogManager"
    }
}
