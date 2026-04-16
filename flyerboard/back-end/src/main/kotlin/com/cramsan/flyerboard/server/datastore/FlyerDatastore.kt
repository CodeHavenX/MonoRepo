package com.cramsan.flyerboard.server.datastore

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.Flyer
import kotlin.time.Instant

/**
 * Interface defining data operations for flyers.
 */
interface FlyerDatastore {

    /**
     * Inserts a new flyer record. Returns the created [Flyer].
     */
    suspend fun createFlyer(
        title: String,
        description: String,
        filePath: String,
        uploaderId: UserId,
        expiresAt: Instant?,
    ): Result<Flyer>

    /**
     * Retrieves a flyer by [id]. Returns null if not found.
     */
    suspend fun getFlyer(id: FlyerId): Result<Flyer?>

    /**
     * Lists flyers with optional [status] filter and pagination.
     * An optional case-insensitive [query] string is matched against title and description.
     * Results are ordered by `created_at` descending.
     * Returns a [PagedResult] containing the matching page and the total row count.
     */
    suspend fun listFlyers(
        status: FlyerStatus?,
        query: String?,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>>

    /**
     * Updates mutable fields of an existing flyer. Returns the updated [Flyer].
     *
     * Note: a null value for any parameter means "leave unchanged". There is currently no way
     * to explicitly clear [expiresAt] back to null via this method.
     */
    suspend fun updateFlyer(
        id: FlyerId,
        title: String?,
        description: String?,
        filePath: String?,
        status: FlyerStatus?,
        expiresAt: Instant?,
    ): Result<Flyer>

    /**
     * Lists approved flyers whose [expiresAt] is before [now].
     */
    suspend fun listExpiredFlyers(now: Instant): Result<List<Flyer>>

    /**
     * Lists flyers uploaded by [uploaderId] with pagination.
     * Results are ordered by `created_at` descending.
     * Returns a [PagedResult] containing the matching page and the total row count.
     */
    suspend fun listFlyersByUploader(
        uploaderId: UserId,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>>
}
