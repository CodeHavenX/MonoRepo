package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.requests.CreateAssetRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.storage.Storage

/**
 * Datastore for managing storage of assets using Supabase.
 */
class SupabaseStorageDatastore(
    private val storage: Storage,
) : StorageDatastore {
    /**
     * Creates a new asset for the given [request]. Returns the [Result] of the operation with the created [Asset].
     */
    override suspend fun createAsset(
        request: CreateAssetRequest
    ): Result<Asset> = runSuspendCatching(TAG) {
        logD(TAG, "Creating a new asset: %s", request.fileName)
        val bucket = storage.from("timecard-images")
        bucket.upload(request.fileName, request.content) {
            upsert = false
        }
        val assetId = generateAssetId(bucket.bucketId, request.fileName)
        Asset(assetId, request.fileName, request.content)
    }

    /**
     * Retrieves an asset for the given [request]. Returns the [Result] of the operation with the fetched [Asset]
     * if found.
     */
    override suspend fun getAsset(
        request: GetFileRequest
    ): Result<Asset?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting assetId: %s", request.id)
        val bucket = storage.from("timecard-images")
        val bytes = bucket.downloadAuthenticated(request.id.toString())

        val fileName = extractFileNameFromAssetId(request.id)

        Asset(request.id, fileName, bytes)
    }

    /**
     * Generates a unique asset ID based on the bucket name and file name.
     */
    private fun generateAssetId(
        bucketName: String, fileName: String
    ): AssetId {
        val assetId = "$bucketName-$fileName"
        return AssetId(assetId)
    }

    /**
     * Extracts the file name from the asset ID.
     */
    private fun extractFileNameFromAssetId(
        assetId: AssetId
    ): String {
        val parts = assetId.assetId.split("-", limit = 2)
        return if (parts.size == 2) parts[1] else ""
    }

    companion object {
        const val TAG = "SupabaseStorageDatastore"
    }
}
