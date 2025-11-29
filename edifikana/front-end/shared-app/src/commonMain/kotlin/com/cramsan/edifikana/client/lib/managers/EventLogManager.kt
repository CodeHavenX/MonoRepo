package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.EventLogCache
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manager for event logs.
 */
class EventLogManager(
    private val eventLogService: EventLogService,
    private val eventLogCache: EventLogCache,
    private val dependencies: ManagerDependencies,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    /**
     * Get all event log records.
     */
    suspend fun getRecords(propertyId: PropertyId): Result<List<EventLogRecordModel>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getRecords")

        val cachedData = eventLogCache.getRecords()

        val onlineData = eventLogService.getRecords(propertyId)
            .getOrThrow()

        (cachedData + onlineData).sortedByDescending { it.timeRecorded }
    }

    /**
     * Get a specific event log record.
     */
    suspend fun getRecord(
        eventLogRecordPK: EventLogEntryId,
    ): Result<EventLogRecordModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getRecord")
        val record = eventLogService.getRecord(eventLogRecordPK).getOrThrow()
        record.copy(
            attachments = record.attachments,
        )
    }

    /**
     * Add a new event log record.
     */
    suspend fun addRecord(eventLogRecord: EventLogRecordModel) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addRecord")
        eventLogCache.addRecord(eventLogRecord)

        coroutineScope {
            uploadRecord(eventLogRecord)
            triggerFullUpload()
        }
        Unit
    }

    private suspend fun uploadRecord(eventLogRecord: EventLogRecordModel) = runCatching {
        logI(TAG, "uploadRecord")
        mutex.withLock {
            eventLogService.addRecord(eventLogRecord).getOrThrow()

            eventLogCache.deleteRecord(eventLogRecord)
        }
    }.onFailure { logE(TAG, "Failed to upload event record", it) }

    private suspend fun triggerFullUpload(): Job {
        logI(TAG, "triggerFullUpload")
        uploadJob?.cancel()
        return dependencies.appScope.launch {
            val pending = eventLogCache.getRecords()

            pending.forEach { record ->
                uploadRecord(record)
            }
        }.also {
            uploadJob = it
        }
    }

    /**
     * Start the upload process.
     */
    suspend fun startUpload(): Result<Job> = dependencies.getOrCatch(TAG) {
        logI(TAG, "startUpload")
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "EventLogManager"
    }
}
