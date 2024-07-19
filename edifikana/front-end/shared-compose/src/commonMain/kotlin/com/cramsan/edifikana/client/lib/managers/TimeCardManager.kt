package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.lib.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.lib.managers.mappers.toEntity
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.client.lib.utils.launch
import com.cramsan.edifikana.client.lib.utils.processImageData
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.storage.FOLDER_TIME_CARDS
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TimeCardManager(
    private val timeCardService: TimeCardService,
    private val timeCardRecordDao: TimeCardRecordDao,
    private val storageService: StorageService,
    private val workContext: WorkContext,
    private val ioDependencies: IODependencies,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    suspend fun getRecords(employeePK: EmployeePK): Result<List<TimeCardRecordModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getRecords")
        val cachedData = timeCardRecordDao.getAll(employeePK.documentPath).map { it.toDomainModel() }

        val onlineData = timeCardService.getRecords(employeePK).getOrThrow()
        (cachedData + onlineData).sortedByDescending { it.eventTime }
    }

    suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getAllRecords")
        val cachedData = timeCardRecordDao.getAll().map { it.toDomainModel() }

        val onlineData = timeCardService.getAllRecords().getOrThrow()
        (cachedData + onlineData).sortedByDescending { it.eventTime }
    }

    suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecordModel> = workContext.getOrCatch(
        TAG
    ) {
        logI(TAG, "getRecord")
        timeCardService.getRecord(timeCardRecordPK).getOrThrow()
    }

    suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: CoreUri) = workContext.getOrCatch(TAG) {
        logI(TAG, "addRecord")
        val entity = timeCardRecord.toEntity(cachedImageUrl)
        timeCardRecordDao.insert(entity)

        workContext.launch(TAG) {
            uploadRecord(entity)
            triggerFullUpload()
        }
        Unit
    }

    @Suppress("MagicNumber")
    private suspend fun uploadRecord(entity: TimeCardRecordEntity) = runCatching {
        mutex.withLock {
            val localImageUri = CoreUri.createUri(requireNotNull(entity.cachedImageUrl))

            val remoteImageRef = runCatching {
                val imageData = readBytes(localImageUri, ioDependencies).getOrThrow()
                val processedImage = processImageData(imageData).getOrThrow()

                val fileName = localImageUri.getFilename(ioDependencies)
                val uploadPath = listOf(FOLDER_TIME_CARDS)

                storageService.uploadFile(
                    processedImage,
                    StorageRef(
                        fileName,
                        uploadPath,
                    ),
                ).getOrThrow()
            }

            val imageUrl = remoteImageRef.getOrThrow().ref
            val processedRecord = entity.toDomainModel().copy(imageUrl = imageUrl)

            timeCardService.addRecord(processedRecord).getOrThrow()

            timeCardRecordDao.delete(entity)
        }
    }.onFailure { logE(TAG, "Failed to upload time card", it) }

    private suspend fun triggerFullUpload(): Job {
        uploadJob?.cancel()
        return workContext.launch(TAG) {
            val pending = timeCardRecordDao.getAll()

            pending.forEach { record ->
                uploadRecord(record)
            }
        }.also {
            uploadJob = it
        }
    }

    suspend fun startUpload(): Result<Job> = workContext.getOrCatch(TAG) {
        triggerFullUpload()
    }

    companion object {
        private const val TAG = "TimeCardManager"
    }
}
