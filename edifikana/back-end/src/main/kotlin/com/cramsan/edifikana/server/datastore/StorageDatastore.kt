package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.framework.annotations.BackendDatastore

/**
 * Interface for the storage datastore.
 */
@BackendDatastore
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

    /**
     * Creates a signed upload URL for the given [fileName]. Returns the signed URL and storage path.
     */
    suspend fun createSignedUploadUrl(fileName: String): Result<Pair<String, String>>
}
