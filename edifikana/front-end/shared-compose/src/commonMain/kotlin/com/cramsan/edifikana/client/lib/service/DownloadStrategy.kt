package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.core.CoreUri

/**
 * Strategy for downloading files.
 */
interface DownloadStrategy {

    /**
     * Check if the file is cached.
     */
    fun isFileCached(targetRef: String): Boolean

    /**
     * Get the cached file.
     */
    fun getCachedFile(targetRef: String): CoreUri

    /**
     * Save the file to the cache.
     */
    fun saveToFile(data: ByteArray, targetRef: String): CoreUri
}
