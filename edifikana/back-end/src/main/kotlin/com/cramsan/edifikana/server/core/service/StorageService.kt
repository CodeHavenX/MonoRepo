package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.framework.logging.logD

/**
 * Service for managing storage of files (assets).
 */
class StorageService(
    private val storageDatastore: StorageDatastore,
) {
    /**
     * Creates a file with the provided [fileName] and [content].
     */
    suspend fun createAsset(
        fileName: String,
        content: ByteArray
    ): Asset {
        logD(TAG, "createFile")
        return storageDatastore.createAsset(
            fileName = fileName,
            content = content,
        ).getOrThrow()
    }

    /**
     * Retrieves a file with the provided [id] if it exists.
     */
    suspend fun getAsset(
        id: AssetId,
    ): Asset? {
        logD(TAG, "getFile")
        val file = storageDatastore.getAsset(
            id = id,
        ).getOrNull()

        return file
    }

    /**
     * Companion object for logging purposes.
     */
    companion object {
        private const val TAG = "StorageService"
    }
}
