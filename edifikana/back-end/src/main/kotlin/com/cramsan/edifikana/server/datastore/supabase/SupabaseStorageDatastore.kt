package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.server.datastore.StorageDatastore
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.minutes

/**
 * Datastore for managing storage of assets using Supabase.
 */
@BackendDatastore
class SupabaseStorageDatastore(private val storage: Storage) : StorageDatastore {
    /**
     * Retrieves an asset by [id], returning a short-lived signed download URL.
     */
    override suspend fun getSignedDownloadUrl(
        id: AssetId,
    ): Result<Asset?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting assetId: %s", id)
            val fileIdParts = extractFileIdPartsFromAssetId(id)
            val bucketId = fileIdParts.dropLast(1).joinToString("/")
            val fileName = fileIdParts.last()

            val bucket = storage.from(bucketId)
            val signedUrl = bucket.createSignedUrl(fileName, expiresIn = 3.minutes)

            Asset(id, fileName, signedUrl)
        }

    /**
     * Creates a signed upload URL for [fileName] in the timecard-images bucket.
     */
    override suspend fun createSignedUploadUrl(
        fileName: String,
        bucketId: String,
    ): Result<Pair<String, String>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating signed upload URL for: %s in bucket: %s", fileName, bucketId)
            val bucket = storage.from(bucketId)
            val signedUpload = bucket.createSignedUploadUrl(fileName)
            Pair(signedUpload.url, signedUpload.path)
        }

    /**
     * Generates a unique asset ID based on the bucket name and file name.
     */
    private fun generateAssetId(
        bucketName: String,
        fileName: String,
    ): AssetId {
        val assetId = "$bucketName/$fileName"
        return AssetId(assetId)
    }

    /**
     * Extracts the file name from the asset ID.
     */
    private fun extractFileIdPartsFromAssetId(
        assetId: AssetId,
    ): List<String> {
        val parts = assetId.assetId.split("/")
        return parts
    }

    companion object {
        const val TAG = "SupabaseStorageDatastore"
    }
}
