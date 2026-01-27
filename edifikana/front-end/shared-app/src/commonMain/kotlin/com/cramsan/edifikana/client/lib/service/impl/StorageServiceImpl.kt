@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.runSuspendCatching
import io.github.jan.supabase.storage.Storage

/**
 * Supabase implementation of [StorageService].
 */
class StorageServiceImpl(private val storage: Storage, private val downloadStrategy: DownloadStrategy) :
    StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> = runSuspendCatching(TAG) {
        val bucket = storage.from(BUCKET_NAME)
        bucket.upload(targetRef, data) {
            upsert = false
        }
        targetRef
    }

    override suspend fun downloadFile(targetRef: String): Result<CoreUri> = runSuspendCatching(TAG) {
        if (downloadStrategy.isFileCached(targetRef)) {
            return@runSuspendCatching downloadStrategy.getCachedFile(targetRef)
        }

        val bucket = storage.from(BUCKET_NAME)
        val bytes = bucket.downloadAuthenticated(targetRef)

        val uri = downloadStrategy.saveToFile(bytes, targetRef)
        uri
    }

    companion object {
        private const val TAG = "StorageServiceImpl"
        private const val BUCKET_NAME = "time_card_events"
    }
}
