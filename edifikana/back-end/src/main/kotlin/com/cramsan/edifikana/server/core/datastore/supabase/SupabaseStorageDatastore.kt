package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.service.models.Asset
import com.cramsan.edifikana.server.core.service.models.requests.CreateAssetRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datastore for managing storage of assets using Supabase.
 */
class SupabaseStorageDatastore(
    private val postgrest: Postgrest,
): StorageDatastore {
    override suspend fun createAsset(request: CreateAssetRequest): Result<Asset> {
        TODO("Not yet implemented")
    }

    override suspend fun getAsset(request: GetFileRequest): Result<Asset?> {
        TODO("Not yet implemented")
    }
}