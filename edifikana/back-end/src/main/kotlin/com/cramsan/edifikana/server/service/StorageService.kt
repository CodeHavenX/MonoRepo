package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.server.datastore.StorageDatastore
import com.cramsan.edifikana.server.service.models.Asset
import com.cramsan.framework.annotations.BackendService
import com.cramsan.framework.logging.logD

/**
 * Service for managing storage of files (assets).
 */
@BackendService
class StorageService(private val storageDatastore: StorageDatastore) {
    /**
     * Retrieves a file with the provided [id] if it exists.
     */
    suspend fun getAsset(
        id: AssetId,
    ): Asset? {
        logD(TAG, "getFile")
        val file =
            storageDatastore
                .getAsset(
                    id = id,
                ).getOrNull()

        return file
    }

    /**
     * Returns a signed upload URL and storage path for [fileName].
     */
    suspend fun getSignedUploadUrl(fileName: String): Pair<String, String> {
        logD(TAG, "getSignedUploadUrl")
        return storageDatastore.createSignedUploadUrl(fileName).getOrThrow()
    }

    /**
     * Companion object for logging purposes.
     */
    companion object {
        private const val TAG = "StorageService"
    }
}
