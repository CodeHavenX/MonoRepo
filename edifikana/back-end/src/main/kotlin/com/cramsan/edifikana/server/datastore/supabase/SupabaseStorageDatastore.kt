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
            val (bucketId, objectPath) = extractBucketAndObjectPath(id)
            val bucket = storage.from(bucketId)
            val signedUrl = bucket.createSignedUrl(objectPath, expiresIn = 3.minutes)
            Asset(id, objectPath, signedUrl)
        }

    /**
     * Creates a signed upload URL for [fileName] in the timecard-images bucket.
     */
    override suspend fun createSignedUploadUrl(
        fileName: String,
        bucketId: String,
    ): Result<Asset> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating signed upload URL for: %s in bucket: %s", fileName, bucketId)
            val bucket = storage.from(bucketId)
            val signedUpload = bucket.createSignedUploadUrl(fileName)
            val assetId = generateAssetId(bucketId, fileName)
            Asset(assetId, fileName, signedUpload.url)
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
     * Splits an [AssetId] into its bucket name and full object path.
     *
     * Contract: AssetId format is `<bucket>/<objectPath>` where the first `/`-delimited
     * segment is the Supabase bucket name and the remainder is the full object path
     * within that bucket (which may itself contain slashes for nested objects).
     */
    internal fun extractBucketAndObjectPath(assetId: AssetId): Pair<String, String> {
        val parts = assetId.assetId.split("/", limit = 2)
        require(parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()) {
            "AssetId must be in '<bucket>/<objectPath>' format, got: ${assetId.assetId}"
        }
        return parts[0] to parts[1]
    }

    companion object {
        const val TAG = "SupabaseStorageDatastore"
    }
}
