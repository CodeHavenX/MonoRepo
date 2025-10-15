package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.server.datastore.StorageDatastore
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.minutes

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
        fileName: String,
        content: ByteArray,
    ): Result<Asset> = runSuspendCatching(TAG) {
        logD(TAG, "Creating a new asset: %s", fileName)
        val bucket = storage.from("images/timecard-images")
        bucket.upload(fileName, content) {
            upsert = false
        }
        val assetId = generateAssetId(bucket.bucketId, fileName)
        Asset(assetId, fileName, null, content)
    }

    /**
     * Retrieves an asset for the given [request]. Returns the [Result] of the operation with the fetched [Asset]
     * if found.
     */
    override suspend fun getAsset(
        id: AssetId,
    ): Result<Asset?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting assetId: %s", id)
        // Extract the file's bucketId and file name from the assetId
        val fileIdParts = extractFileIdPartsFromAssetId(id)
        val bucketId = fileIdParts.dropLast(1).joinToString("/")
        val fileName = fileIdParts.last()

        // Download the file from the storage bucket
        val bucket = storage.from(bucketId)
        val signedUrl = bucket.createSignedUrl(fileName, expiresIn = 3.minutes)
        val bytes = bucket.downloadAuthenticated(fileName)

        // Create and return the Asset object
        Asset(id, fileName, signedUrl, bytes)
    }

    /**
     * Generates a unique asset ID based on the bucket name and file name.
     */
    private fun generateAssetId(
        bucketName: String,
        fileName: String
    ): AssetId {
        val assetId = "$bucketName/$fileName"
        return AssetId(assetId)
    }

    /**
     * Extracts the file name from the asset ID.
     */
    private fun extractFileIdPartsFromAssetId(
        assetId: AssetId
    ): List<String> {
        val parts = assetId.assetId.split("/")
        return parts
    }

    companion object {
        const val TAG = "SupabaseStorageDatastore"
    }
}
