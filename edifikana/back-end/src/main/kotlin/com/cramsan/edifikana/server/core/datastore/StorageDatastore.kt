package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.server.core.service.models.Asset

/**
 * Interface for the storage datastore.
 */
interface StorageDatastore {
    /**
     * Creates a new file with the given [fileName] and [content]. Returns the [Result] of the operation with the created [Asset].
     */
    suspend fun createAsset(
        fileName: String,
        content: ByteArray,
    ): Result<Asset>

    /**
     * Retrieves a file with the given [id]. Returns the [Result] of the operation with the fetched [Asset] if found.
     */
    suspend fun getAsset(
        id: AssetId,
    ): Result<Asset?>
}
