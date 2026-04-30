package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.CoreUri

/**
 * Service for managing storage.
 */
@FrontendService
interface StorageService {
    /**
     * Upload a file to [bucketId] at the path [targetRef].
     */
    suspend fun uploadFile(data: ByteArray, targetRef: String, bucketId: String): Result<String>

    /**
     * Download a file.
     */
    suspend fun downloadFile(targetRef: String): Result<CoreUri>
}
