package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.framework.core.CoreUri
import io.github.jan.supabase.storage.Storage

class SupaStorageService(
    private val storage: Storage,
    private val downloadStrategy: DownloadStrategy,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: StorageRef): Result<StorageRef> = runSuspendCatching(
        TAG
    ) {
        val bucket = storage.from("test")

        val fetchOutcome = runCatching {
            fetchBytes(targetRef)
        }
        val fileExists = fetchOutcome.isSuccess

        if (fileExists) {
            bucket.update(targetRef.ref, data)
        } else {
            bucket.upload(targetRef.ref, data)
        }

        StorageRef(targetRef.filename(), targetRef.path())
    }

    override suspend fun downloadImage(targetRef: StorageRef): Result<CoreUri> = runSuspendCatching(TAG) {
        if (downloadStrategy.isFileCached(targetRef)) {
            return@runSuspendCatching downloadStrategy.getCachedFile(targetRef)
        }

        val bytes = fetchBytes(targetRef)

        downloadStrategy.saveToFile(bytes, targetRef)
    }

    private suspend fun fetchBytes(targetRef: StorageRef): ByteArray {
        val bucket = storage.from("test")
        return bucket.downloadAuthenticated(targetRef.ref)
    }

    companion object {
        private const val TAG = "SupaStorageService"
    }
}
