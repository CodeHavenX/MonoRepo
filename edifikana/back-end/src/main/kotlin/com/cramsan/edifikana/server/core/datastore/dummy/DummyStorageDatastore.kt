package com.cramsan.edifikana.server.core.datastore.dummy

import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.requests.CreateAssetRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest
import com.cramsan.framework.logging.logD

/**
 * Dummy implementation of StorageDatastore for development and testing.
 */
class DummyStorageDatastore : StorageDatastore {
    override suspend fun createAsset(request: CreateAssetRequest): Result<Asset> {
        logD(TAG, "createAsset: %s", request.fileName)
        // Always return ASSET_1 for simplicity
        return Result.success(ASSET_1)
    }

    override suspend fun getAsset(request: GetFileRequest): Result<Asset?> {
        logD(TAG, "getAsset: %s", request.id)
        // Return ASSET_1 or ASSET_2 based on id for demonstration
        return when (request.id.toString()) {
            ASSET_1.id.toString() -> Result.success(ASSET_1)
            ASSET_2.id.toString() -> Result.success(ASSET_2)
            else -> Result.success(null)
        }
    }

    companion object {
        private const val TAG = "DummyStorageDatastore"
    }
}
