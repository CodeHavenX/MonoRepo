package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.annotations.BackendService
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.uuid.UUID
import kotlin.time.Instant

/**
 * Business logic service for flyer operations.
 *
 * Validates inputs, orchestrates datastore and file storage calls, enforces ownership, and
 * generates signed URLs for file access.
 */
@BackendService
class FlyerService(private val flyerDatastore: FlyerDatastore, private val fileDatastore: FileDatastore) {
    /**
     * Creates a new flyer with [PENDING][FlyerStatus.PENDING] status.
     *
     * Generates the flyer's [FlyerId] server-side, sanitizes title/description, inserts a new
     * flyer record with `file_path` equal to the generated ID, and returns a signed upload URL
     * for that asset.
     */
    suspend fun createFlyer(
        uploaderId: UserId,
        title: String,
        description: String,
        expiresAt: Instant?,
    ): Result<Pair<Flyer, SignedUpload>> {
        logD(TAG, "createFlyer for uploader=%s", uploaderId)

        val id = FlyerId(UUID.random().toString())
        val sanitizedTitle = InputSanitizer.sanitizeText(title, maxLength = TITLE_MAX_LENGTH)
        val sanitizedDescription = InputSanitizer.sanitizeText(description, maxLength = DESCRIPTION_MAX_LENGTH)

        val flyer =
            flyerDatastore
                .createFlyer(
                    id = id,
                    title = sanitizedTitle,
                    description = sanitizedDescription,
                    filePath = id.flyerId,
                    uploaderId = uploaderId,
                    expiresAt = expiresAt,
                ).getOrElse { return Result.failure(it) }

        val upload =
            fileDatastore
                .createSignedUploadUrl(flyer.filePath)
                .getOrElse { return Result.failure(it) }

        return Result.success(flyer to upload)
    }

    /**
     * Retrieves a flyer by [flyerId] and attaches a signed URL for the file.
     * Returns null if not found.
     */
    suspend fun getFlyer(flyerId: FlyerId): Result<Flyer?> {
        logD(TAG, "getFlyer: %s", flyerId)
        return flyerDatastore.getFlyer(flyerId).map { flyer ->
            if (flyer == null) return@map null
            val fileUrl =
                fileDatastore
                    .getSignedUrl(flyer.filePath)
                    .onFailure { logE(TAG, "Failed to get signed URL for ${flyer.filePath}", it) }
                    .getOrNull()
            flyer.copy(fileUrl = fileUrl)
        }
    }

    /**
     * Lists flyers with an optional [status] filter and full-text [query] and pagination,
     * attaching signed URLs. The [query] string is matched case-insensitively against title
     * and description. Pass null to skip text search.
     */
    suspend fun listFlyers(
        status: FlyerStatus?,
        query: String?,
        offset: Int,
        limit: Int,
    ): Result<PaginatedList<Flyer>> {
        logD(TAG, "listFlyers status=%s query=%s offset=%d limit=%d", status, query, offset, limit)
        return flyerDatastore.listFlyers(status, query, offset, limit).map { page ->
            val withUrls =
                page.items.map { flyer ->
                    val fileUrl =
                        fileDatastore
                            .getSignedUrl(flyer.filePath)
                            .onFailure { logE(TAG, "Failed to get signed URL for ${flyer.filePath}", it) }
                            .getOrNull()
                    flyer.copy(fileUrl = fileUrl)
                }
            PaginatedList(items = withUrls, total = page.total.toInt(), offset = offset, limit = limit)
        }
    }

    /**
     * Updates an existing flyer. The caller must be the original uploader.
     *
     * Any provided fields are sanitized and applied. If [requestUpload] is `true`, a fresh
     * signed upload URL is generated for the flyer's existing asset path. Any edit, including
     * requesting an upload, resets status to [PENDING][FlyerStatus.PENDING] to trigger
     * re-moderation.
     */
    suspend fun updateFlyer(
        flyerId: FlyerId,
        requesterId: UserId,
        title: String?,
        description: String?,
        expiresAt: Instant?,
        requestUpload: Boolean,
    ): Result<Pair<Flyer, SignedUpload?>> {
        logD(TAG, "updateFlyer: %s requester=%s", flyerId, requesterId)

        val existing =
            flyerDatastore
                .getFlyer(flyerId)
                .getOrElse { return Result.failure(it) }
                ?: return Result.failure(
                    ClientRequestExceptions.NotFoundException("Flyer not found: ${flyerId.flyerId}"),
                )

        if (existing.uploaderId != requesterId) {
            return Result.failure(
                ClientRequestExceptions.ForbiddenException(
                    "User ${requesterId.userId} does not own flyer ${flyerId.flyerId}",
                ),
            )
        }

        val sanitizedTitle = title?.let { InputSanitizer.sanitizeText(it, maxLength = TITLE_MAX_LENGTH) }
        val sanitizedDescription =
            description?.let { InputSanitizer.sanitizeText(it, maxLength = DESCRIPTION_MAX_LENGTH) }

        val flyer =
            flyerDatastore
                .updateFlyer(
                    id = flyerId,
                    title = sanitizedTitle,
                    description = sanitizedDescription,
                    status = FlyerStatus.PENDING,
                    expiresAt = expiresAt,
                ).getOrElse { return Result.failure(it) }

        val upload =
            if (requestUpload) {
                fileDatastore
                    .createSignedUploadUrl(flyer.filePath)
                    .getOrElse { return Result.failure(it) }
            } else {
                null
            }

        return Result.success(flyer to upload)
    }

    /**
     * Lists flyers uploaded by [uploaderId] with pagination, attaching signed URLs.
     */
    suspend fun listFlyersByUploader(
        uploaderId: UserId,
        offset: Int,
        limit: Int,
    ): Result<PaginatedList<Flyer>> {
        logD(TAG, "listFlyersByUploader: %s offset=%d limit=%d", uploaderId, offset, limit)
        return flyerDatastore.listFlyersByUploader(uploaderId, offset, limit).map { page ->
            val withUrls =
                page.items.map { flyer ->
                    val fileUrl =
                        fileDatastore
                            .getSignedUrl(flyer.filePath)
                            .onFailure { logE(TAG, "Failed to get signed URL for ${flyer.filePath}", it) }
                            .getOrNull()
                    flyer.copy(fileUrl = fileUrl)
                }
            PaginatedList(items = withUrls, total = page.total.toInt(), offset = offset, limit = limit)
        }
    }

    companion object {
        private const val TAG = "FlyerService"
        private const val TITLE_MAX_LENGTH = 200
        private const val DESCRIPTION_MAX_LENGTH = 2000
    }
}
