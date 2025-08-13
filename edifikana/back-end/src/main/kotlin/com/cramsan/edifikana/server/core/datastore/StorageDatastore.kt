package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.requests.CreateAssetRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest

/**
 * Interface for the storage datastore.
 */
interface StorageDatastore {
    /**
     * Creates a new file for the given [request]. Returns the [Result] of the operation with the created [Asset].
     */
    suspend fun createAsset(
        request: CreateAssetRequest,
    ): Result<Asset>

    /**
     * Retrieves a file for the given [request]. Returns the [Result] of the operation with the fetched [Asset] if found.
     */
    suspend fun getAsset(
        request: GetFileRequest,
    ): Result<Asset?>
}
