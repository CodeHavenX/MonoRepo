package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.lib.model.network.asset.StorageResourceType
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.CoreUri

/**
 * Service for managing storage.
 */
@FrontendService
interface StorageService {
    /**
     * Upload a file to [bucketId] at the path [targetRef].
     * [resourceType] and [resourceId] identify the domain resource this asset belongs to
     * and are used by the backend to enforce org-level authorization.
     */
    suspend fun uploadFile(
        data: ByteArray,
        targetRef: String,
        bucketId: String,
        resourceType: StorageResourceType,
        resourceId: String,
    ): Result<String>

    /**
     * Download a file.
     * [resourceType] and [resourceId] identify the domain resource this asset belongs to
     * and are used by the backend to enforce org-level authorization.
     */
    suspend fun downloadFile(
        targetRef: String,
        resourceType: StorageResourceType,
        resourceId: String,
    ): Result<CoreUri>
}
