package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.flyerboard.server.service.models.PaginatedList
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions

/**
 * Business logic service for moderation operations.
 *
 * All mutating operations verify that the acting user holds the [ADMIN][UserRole.ADMIN] role before
 * performing any status transition.
 */
class ModerationService(
    private val flyerDatastore: FlyerDatastore,
    private val fileDatastore: FileDatastore,
    private val userProfileDatastore: UserProfileDatastore,
) {

    /**
     * Lists flyers with [PENDING][FlyerStatus.PENDING] status, with pagination and signed URLs.
     */
    suspend fun listPendingFlyers(offset: Int, limit: Int): Result<PaginatedList<Flyer>> {
        logD(TAG, "listPendingFlyers offset=%d limit=%d", offset, limit)
        return flyerDatastore.listFlyers(FlyerStatus.PENDING, query = null, offset, limit).map { page ->
            val withUrls = page.items.map { flyer ->
                val fileUrl = fileDatastore.getSignedUrl(flyer.filePath)
                    .onFailure { logE(TAG, "Failed to get signed URL for ${flyer.filePath}", it) }
                    .getOrNull()
                flyer.copy(fileUrl = fileUrl)
            }
            PaginatedList(items = withUrls, total = page.total.toInt(), offset = offset, limit = limit)
        }
    }

    /**
     * Transitions a flyer from [PENDING][FlyerStatus.PENDING] to [APPROVED][FlyerStatus.APPROVED].
     *
     * Requires [adminUserId] to hold the [ADMIN][UserRole.ADMIN] role.
     */
    suspend fun approveFlyer(flyerId: FlyerId, adminUserId: UserId): Result<Flyer> {
        logD(TAG, "approveFlyer: %s by admin=%s", flyerId, adminUserId)
        verifyAdmin(adminUserId).getOrElse { return Result.failure(it) }
        return moderateFlyer(flyerId, FlyerStatus.APPROVED)
    }

    /**
     * Transitions a flyer from [PENDING][FlyerStatus.PENDING] to [REJECTED][FlyerStatus.REJECTED]
     * and deletes its associated file from storage.
     *
     * Requires [adminUserId] to hold the [ADMIN][UserRole.ADMIN] role.
     */
    suspend fun rejectFlyer(flyerId: FlyerId, adminUserId: UserId): Result<Flyer> {
        logD(TAG, "rejectFlyer: %s by admin=%s", flyerId, adminUserId)
        verifyAdmin(adminUserId).getOrElse { return Result.failure(it) }
        val existing = flyerDatastore.getFlyer(flyerId)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(
                ClientRequestExceptions.NotFoundException("Flyer not found: ${flyerId.flyerId}")
            )
        return moderateFlyer(flyerId, FlyerStatus.REJECTED).also { result ->
            if (result.isSuccess) {
                fileDatastore.deleteFile(existing.filePath)
                    .onFailure { logE(TAG, "Failed to delete file ${existing.filePath} for rejected flyer ${flyerId.flyerId}", it) }
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Verifies that [userId] holds the [ADMIN][UserRole.ADMIN] role.
     *
     * Returns [Result.failure] with [ClientRequestExceptions.UnauthorizedException] if the user
     * has no profile, or [ClientRequestExceptions.ForbiddenException] if the user exists but is
     * not an admin.
     */
    private suspend fun verifyAdmin(userId: UserId): Result<Unit> {
        val profile = userProfileDatastore.getUserProfile(userId)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(
                ClientRequestExceptions.UnauthorizedException(
                    "User ${userId.userId} does not have a profile"
                )
            )

        if (profile.role != UserRole.ADMIN) {
            return Result.failure(
                ClientRequestExceptions.ForbiddenException(
                    "User ${userId.userId} is not an admin"
                )
            )
        }
        return Result.success(Unit)
    }

    /**
     * Verifies the flyer exists, updates its status, and attaches a signed URL.
     */
    private suspend fun moderateFlyer(flyerId: FlyerId, newStatus: FlyerStatus): Result<Flyer> {
        flyerDatastore.getFlyer(flyerId)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(
                ClientRequestExceptions.NotFoundException("Flyer not found: ${flyerId.flyerId}")
            )

        return flyerDatastore.updateFlyer(
            id = flyerId,
            title = null,
            description = null,
            filePath = null,
            status = newStatus,
            expiresAt = null,
        ).map { flyer ->
            val fileUrl = fileDatastore.getSignedUrl(flyer.filePath)
                .onFailure { logE(TAG, "Failed to get signed URL for ${flyer.filePath}", it) }
                .getOrNull()
            flyer.copy(fileUrl = fileUrl)
        }
    }

    companion object {
        private const val TAG = "ModerationService"
    }
}
