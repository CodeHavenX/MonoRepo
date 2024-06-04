package com.cramsan.edifikana.client.android.managers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.net.toUri
import com.cramsan.edifikana.client.android.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.android.db.models.TimeCardRecordEntity
import com.cramsan.edifikana.client.android.managers.mappers.toDomainModel
import com.cramsan.edifikana.client.android.managers.mappers.toEntity
import com.cramsan.edifikana.client.android.managers.mappers.toFirebaseModel
import com.cramsan.edifikana.client.android.models.StorageRef
import com.cramsan.edifikana.client.android.models.TimeCardRecordModel
import com.cramsan.edifikana.client.android.utils.getFilename
import com.cramsan.edifikana.client.android.utils.getOrCatch
import com.cramsan.edifikana.client.android.utils.launch
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.TimeCardRecord
import com.cramsan.edifikana.lib.firestore.TimeCardRecordPK
import com.cramsan.edifikana.lib.storage.FOLDER_TIME_CARDS
import com.cramsan.framework.logging.logE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class TimeCardManager @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val timeCardRecordDao: TimeCardRecordDao,
    private val storageService: StorageService,
    private val workContext: WorkContext,
) {
    private val mutex = Mutex()
    private var uploadJob: Job? = null

    @OptIn(FireStoreModel::class)
    suspend fun getRecords(employeePK: EmployeePK): Result<List<TimeCardRecordModel>> = workContext.getOrCatch(TAG) {
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(2.days).epochSeconds

        val cachedData = timeCardRecordDao.getAll(employeePK.documentPath).map { it.toDomainModel() }

        val onlineData = fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("eventTime", Query.Direction.DESCENDING)
            .whereGreaterThan("eventTime", twoDaysAgo)
            .whereEqualTo("employeeDocumentId", employeePK.documentPath)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList().map { it.toDomainModel(workContext.storageBucket) }
        (cachedData + onlineData).sortedByDescending { it.eventTime }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> = workContext.getOrCatch(TAG) {
        val now = workContext.clock.now()

        // TODO: Make this range configurable
        val twoDaysAgo = now.minus(3.days).epochSeconds

        val cachedData = timeCardRecordDao.getAll().map { it.toDomainModel() }

        val onlineData = fireStore.collection(TimeCardRecord.COLLECTION)
            .orderBy("eventTime", Query.Direction.DESCENDING)
            .whereGreaterThan("eventTime", twoDaysAgo)
            .get()
            .await()
            .toObjects(TimeCardRecord::class.java).toList().map { it.toDomainModel(workContext.storageBucket) }
        (cachedData + onlineData).sortedByDescending { it.eventTime }
    }

    @OptIn(FireStoreModel::class)
    suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecordModel> = workContext.getOrCatch(
        TAG
    ) {
        val result = fireStore.collection(TimeCardRecord.COLLECTION)
            .document(timeCardRecordPK.documentPath)
            .get()
            .await()
            .toObject(TimeCardRecord::class.java) ?: throw RuntimeException(
            "TimeCardRecord $timeCardRecordPK not found"
        )
        result.toDomainModel(workContext.storageBucket)
    }

    suspend fun addRecord(timeCardRecord: TimeCardRecordModel, cachedImageUrl: Uri) = workContext.getOrCatch(TAG) {
        val entity = timeCardRecord.toEntity(cachedImageUrl)
        timeCardRecordDao.insert(entity)

        workContext.launch(TAG) {
            uploadRecord(entity)
            triggerFullUpload()
        }
        Unit
    }

    @Suppress("MagicNumber")
    @OptIn(FireStoreModel::class)
    private suspend fun uploadRecord(entity: TimeCardRecordEntity) = runCatching {
        mutex.withLock {
            val localImageUri = entity.cachedImageUrl?.toUri()

            val remoteImageRef = runCatching {
                requireNotNull(localImageUri) { "Local image url is invalid: $localImageUri" }

                workContext.appContext.contentResolver.openInputStream(localImageUri).use { inputStream ->
                    val imageData = inputStream?.readBytes() ?: throw RuntimeException(
                        "Could not get inputstream for uri: $localImageUri"
                    )

                    val exifInterface = ExifInterface(ByteArrayInputStream(imageData))
                    val rotation = when (
                        exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                    ) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }

                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    val matrix = Matrix().apply { postRotate(rotation) }
                    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                    val stream = ByteArrayOutputStream()
                    // TODO: Set the compression to be configurable
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)
                    val byteArray = stream.toByteArray()
                    rotatedBitmap.recycle()

                    val fileName = localImageUri.getFilename(workContext.appContext.contentResolver)
                    val uploadPath = listOf(FOLDER_TIME_CARDS)

                    storageService.uploadFile(
                        byteArray,
                        StorageRef(
                            fileName,
                            uploadPath,
                        ),
                    ).getOrThrow()
                }
            }.onFailure {
                logE(TAG, "Failed to upload image.", it)
            }

            val imageUrl = remoteImageRef.getOrThrow().ref
            val processedRecord = entity.toFirebaseModel().copy(imageUrl = imageUrl)

            fireStore.collection(TimeCardRecord.COLLECTION)
                .document(processedRecord.documentId().documentPath)
                .set(processedRecord)
                .await()

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

    companion object {
        private const val TAG = "TimeCardManager"
    }
}
