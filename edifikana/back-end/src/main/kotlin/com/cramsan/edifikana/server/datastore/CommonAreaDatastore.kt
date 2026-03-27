package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.service.models.CommonArea

/**
 * Interface for the common area datastore.
 */
interface CommonAreaDatastore {

    /**
     * Creates a new common area record. Returns the [Result] with the created [CommonArea].
     */
    suspend fun createCommonArea(
        propertyId: PropertyId,
        name: String,
        type: CommonAreaType,
        description: String?,
    ): Result<CommonArea>

    /**
     * Retrieves a common area by [commonAreaId]. Returns null if not found or soft-deleted.
     */
    suspend fun getCommonArea(commonAreaId: CommonAreaId): Result<CommonArea?>

    /**
     * Retrieves all non-deleted common areas for the given [propertyId].
     */
    suspend fun getCommonAreasForProperty(propertyId: PropertyId): Result<List<CommonArea>>

    /**
     * Updates the [name], [type], and/or [description] of an existing common area. Returns the updated [CommonArea].
     */
    suspend fun updateCommonArea(
        commonAreaId: CommonAreaId,
        name: String?,
        type: CommonAreaType?,
        description: String?,
    ): Result<CommonArea>

    /**
     * Soft-deletes the common area with the given [commonAreaId]. Returns true if the record was deleted.
     */
    suspend fun deleteCommonArea(commonAreaId: CommonAreaId): Result<Boolean>

    /**
     * Hard-deletes the common area with the given [commonAreaId]. For integration test cleanup only.
     * Implementations SHOULD only purge records that have previously been soft-deleted via [deleteCommonArea].
     *
     * The returned [Result] will be:
     * - `Result.success(true)` if a matching, soft-deleted record existed and was hard-deleted.
     * - `Result.success(false)` if no matching soft-deleted record exists (including when the record does not exist
     *   at all, or exists but has not been soft-deleted).
     */
    suspend fun purgeCommonArea(commonAreaId: CommonAreaId): Result<Boolean>
}
