package com.cramsan.flyerboard.client.lib.service

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus

/**
 * Service for communicating with the FlyerBoard backend flyer and moderation APIs.
 */
interface FlyerService {

    /**
     * Lists publicly visible flyers with optional filtering and pagination.
     */
    suspend fun listFlyers(
        offset: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE,
        status: FlyerStatus? = null,
        query: String? = null,
    ): Result<PaginatedFlyerModel>

    /**
     * Retrieves a single flyer by [flyerId]. Returns null if not found.
     */
    suspend fun getFlyer(flyerId: FlyerId): Result<FlyerModel?>

    /**
     * Uploads a new flyer. Requires authentication.
     */
    suspend fun createFlyer(
        title: String,
        description: String,
        expiresAt: String?,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<FlyerModel>

    /**
     * Updates an existing flyer. Requires authentication and ownership.
     * Only provided fields are updated; null fields are left unchanged.
     * Uploading a new [fileBytes] is optional.
     */
    suspend fun updateFlyer(
        flyerId: FlyerId,
        title: String? = null,
        description: String? = null,
        expiresAt: String? = null,
        fileBytes: ByteArray? = null,
        fileName: String? = null,
        mimeType: String? = null,
    ): Result<FlyerModel>

    /**
     * Lists archived flyers with pagination.
     */
    suspend fun listArchived(
        offset: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<PaginatedFlyerModel>

    /**
     * Lists the authenticated user's own flyers with pagination. Requires authentication.
     */
    suspend fun listMyFlyers(
        offset: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<PaginatedFlyerModel>

    /**
     * Lists pending flyers awaiting moderation. Requires admin authentication.
     */
    suspend fun listPendingFlyers(
        offset: Int = 0,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<PaginatedFlyerModel>

    /**
     * Applies a moderation [action] ("approve" or "reject") to [flyerId].
     * Requires admin authentication.
     */
    suspend fun moderate(flyerId: FlyerId, action: String): Result<FlyerModel>

    companion object {
        /** Default page size for paginated flyer requests. */
        const val DEFAULT_PAGE_SIZE = 20
    }
}
