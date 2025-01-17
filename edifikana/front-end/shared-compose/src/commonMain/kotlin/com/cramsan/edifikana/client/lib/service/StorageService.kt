package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.core.CoreUri

/**
 * Service for managing storage.
 */
interface StorageService {
    /**
     * Upload a file.
     */
    suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String>

    /**
     * Download a file.
     */
    suspend fun downloadFile(targetRef: String): Result<CoreUri>
}
