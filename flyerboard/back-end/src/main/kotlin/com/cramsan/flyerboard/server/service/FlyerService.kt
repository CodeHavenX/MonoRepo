package com.cramsan.flyerboard.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.flyerboard.server.settings.FlyerBoardSettingKey
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import kotlin.time.Instant

/**
 * Business logic service for flyer operations.
 *
 * Validates inputs, orchestrates datastore and file storage calls, enforces ownership, and
 * generates signed URLs for file access.
 */
class FlyerService(
    private val flyerDatastore: FlyerDatastore,
    private val fileDatastore: FileDatastore,
    private val settingsHolder: SettingsHolder,
) {

    /**
     * Creates a new flyer with [PENDING][FlyerStatus.PENDING] status.
     *
     * Validates MIME type and file size, sanitizes title/description, uploads the file, and
     * inserts a new flyer record.
     */
    suspend fun createFlyer(
        uploaderId: UserId,
        title: String,
        description: String,
        expiresAt: Instant?,
        fileContent: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<Flyer> {
        logD(TAG, "createFlyer for uploader=%s", uploaderId)
        validateMimeType(mimeType).getOrElse { return Result.failure(it) }
        validateFileSize(fileContent.size.toLong()).getOrElse { return Result.failure(it) }

        val sanitizedTitle = InputSanitizer.sanitizeText(title, maxLength = TITLE_MAX_LENGTH)
        val sanitizedDescription = InputSanitizer.sanitizeText(description, maxLength = DESCRIPTION_MAX_LENGTH)

        val filePath = fileDatastore.uploadFile(fileName, fileContent)
            .getOrElse { return Result.failure(it) }

        return flyerDatastore.createFlyer(
            title = sanitizedTitle,
            description = sanitizedDescription,
            filePath = filePath,
            uploaderId = uploaderId,
            expiresAt = expiresAt,
        ).map { flyer ->
            val fileUrl = fileDatastore.getSignedUrl(flyer.filePath).getOrNull()
            flyer.copy(fileUrl = fileUrl)
        }
    }

    /**
     * Retrieves a flyer by [flyerId] and attaches a signed URL for the file.
     * Returns null if not found.
     */
    suspend fun getFlyer(flyerId: FlyerId): Result<Flyer?> {
        logD(TAG, "getFlyer: %s", flyerId)
        return flyerDatastore.getFlyer(flyerId).map { flyer ->
            if (flyer == null) return@map null
            val fileUrl = fileDatastore.getSignedUrl(flyer.filePath).getOrNull()
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
            val withUrls = page.items.map { flyer ->
                val fileUrl = fileDatastore.getSignedUrl(flyer.filePath).getOrNull()
                flyer.copy(fileUrl = fileUrl)
            }
            PaginatedList(items = withUrls, total = page.total.toInt(), offset = offset, limit = limit)
        }
    }

    /**
     * Updates an existing flyer. The caller must be the original uploader.
     *
     * Any provided fields are sanitized and applied. Uploading a new file is optional. Status is
     * reset to [PENDING][FlyerStatus.PENDING] to trigger re-moderation.
     */
    suspend fun updateFlyer(
        flyerId: FlyerId,
        requesterId: UserId,
        title: String?,
        description: String?,
        expiresAt: Instant?,
        fileContent: ByteArray?,
        fileName: String?,
        mimeType: String?,
    ): Result<Flyer> {
        logD(TAG, "updateFlyer: %s requester=%s", flyerId, requesterId)

        val existing = flyerDatastore.getFlyer(flyerId)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(
                ClientRequestExceptions.NotFoundException("Flyer not found: ${flyerId.flyerId}")
            )

        if (existing.uploaderId != requesterId) {
            return Result.failure(
                ClientRequestExceptions.ForbiddenException(
                    "User ${requesterId.userId} does not own flyer ${flyerId.flyerId}"
                )
            )
        }

        // Validate and upload new file if provided
        var newFilePath: String? = null
        if (fileContent != null && fileName != null && mimeType != null) {
            validateMimeType(mimeType).getOrElse { return Result.failure(it) }
            validateFileSize(fileContent.size.toLong()).getOrElse { return Result.failure(it) }
            newFilePath = fileDatastore.uploadFile(fileName, fileContent)
                .getOrElse { return Result.failure(it) }
        }

        val sanitizedTitle = title?.let { InputSanitizer.sanitizeText(it, maxLength = TITLE_MAX_LENGTH) }
        val sanitizedDescription =
            description?.let { InputSanitizer.sanitizeText(it, maxLength = DESCRIPTION_MAX_LENGTH) }

        return flyerDatastore.updateFlyer(
            id = flyerId,
            title = sanitizedTitle,
            description = sanitizedDescription,
            filePath = newFilePath,
            status = FlyerStatus.PENDING,
            expiresAt = expiresAt,
        ).map { flyer ->
            val fileUrl = fileDatastore.getSignedUrl(flyer.filePath).getOrNull()
            flyer.copy(fileUrl = fileUrl)
        }
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
            val withUrls = page.items.map { flyer ->
                val fileUrl = fileDatastore.getSignedUrl(flyer.filePath).getOrNull()
                flyer.copy(fileUrl = fileUrl)
            }
            PaginatedList(items = withUrls, total = page.total.toInt(), offset = offset, limit = limit)
        }
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private fun validateMimeType(mimeType: String): Result<Unit> {
        return if (mimeType in ALLOWED_MIME_TYPES) {
            Result.success(Unit)
        } else {
            Result.failure(
                ClientRequestExceptions.InvalidRequestException(
                    "Unsupported file type: $mimeType. Allowed: ${ALLOWED_MIME_TYPES.joinToString()}"
                )
            )
        }
    }

    private fun validateFileSize(sizeBytes: Long): Result<Unit> {
        val maxBytes = settingsHolder.getLong(FlyerBoardSettingKey.MaxFileSizeBytes)
            ?: DEFAULT_MAX_FILE_SIZE_BYTES
        return if (sizeBytes <= maxBytes) {
            Result.success(Unit)
        } else {
            Result.failure(
                ClientRequestExceptions.InvalidRequestException(
                    "File size ${sizeBytes}B exceeds maximum allowed ${maxBytes}B"
                )
            )
        }
    }

    companion object {
        private const val TAG = "FlyerService"
        private const val TITLE_MAX_LENGTH = 200
        private const val DESCRIPTION_MAX_LENGTH = 2000
        private const val DEFAULT_MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L // 10 MB

        private val ALLOWED_MIME_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf",
        )
    }
}
