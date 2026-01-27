package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.TimeCardCache
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.processImageData
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.framework.core.CoreUri
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
 * Manager for time cards.
 */
class TimeCardManager(
    private val timeCardService: TimeCardService,
    private val timeCardCache: TimeCardCache,
    private val storageService: StorageService,
    private val dependencies: ManagerDependencies,
    private val ioDependencies: IODependencies,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    /**
     * Get all time card records for a employee member.
     */
    suspend fun getRecords(employeePK: EmployeeId, propertyId: PropertyId): Result<List<TimeCardRecordModel>> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "getRecords")
            val cachedData = timeCardCache.getRecords(employeePK)

            val onlineData = timeCardService.getRecords(employeePK, propertyId).getOrThrow()
            (cachedData + onlineData).sortedByDescending { it.eventTime }
        }

    /**
     * Get all time card records.
     */
    suspend fun getAllRecords(propertyId: PropertyId): Result<List<TimeCardRecordModel>> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "getAllRecords")
            val cachedData = timeCardCache.getAllRecords()

            val onlineData = timeCardService.getAllRecords(propertyId).getOrThrow()
            (cachedData + onlineData).sortedByDescending { it.eventTime }
        }

    /**
     * Get a specific time card record.
     */
    suspend fun getRecord(timeCardRecordPK: TimeCardEventId): Result<TimeCardRecordModel> = dependencies.getOrCatch(
        TAG,
    ) {
        logI(TAG, "getRecord")
        timeCardService.getRecord(timeCardRecordPK).getOrThrow()
    }

    /**
     * Add a time card record.
     */
    suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri) = dependencies.getOrCatch(TAG) {
        logI(TAG, "addRecord")
        timeCardCache.addRecord(timeCardRecord, cachedImageUrl)

        coroutineScope {
            uploadRecord(timeCardRecord)
            triggerFullUpload()
        }
        Unit
    }

    @Suppress("MagicNumber")
    private suspend fun uploadRecord(model: TimeCardRecordModel) = runCatching {
        mutex.withLock {
            val localImageUri = CoreUri.createUri(requireNotNull(model.imageUrl))

            val remoteImageRef = runCatching {
                val imageData = readBytes(localImageUri, ioDependencies).getOrThrow()
                val processedImage = processImageData(imageData).getOrThrow()

                val fileName = localImageUri.getFilename(ioDependencies)

                storageService.uploadFile(
                    processedImage,
                    fileName,
                ).getOrThrow()
            }

            val imageUrl = remoteImageRef.getOrThrow()
            val processedRecord = model.copy(imageUrl = imageUrl)

            timeCardService.addRecord(processedRecord).getOrThrow()

            timeCardCache.deleteRecord(model)
        }
    }.onFailure { logE(TAG, "Failed to upload time card", it) }

    private suspend fun triggerFullUpload(): Job {
        uploadJob?.cancel()
        return dependencies.appScope.launch {
            val pending = timeCardCache.getAllRecords()

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
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "TimeCardManager"
    }
}
