@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.storage.Storage

/**
 * Supabase implementation of [StorageService].
 */
class StorageServiceImpl(
    private val storage: Storage,
    private val downloadStrategy: DownloadStrategy,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> = runSuspendCatching(TAG) {
        try {
            val bucket = storage.from(BUCKET_NAME)
            bucket.upload(targetRef, data) {
                upsert = false
            }
            targetRef
        } catch (e: RestException) {
            logE(TAG, "Error uploading file: statusCode=${e.statusCode}, message=${e.message}", e)
            // RestException.statusCode is an INT Http Status Code
            throw when (e.statusCode) {
                400 -> ClientRequestExceptions.InvalidRequestException("Invalid file format", e)
                401 -> ClientRequestExceptions.UnauthorizedException("Authentication required", e)
                403 -> ClientRequestExceptions.ForbiddenException("Permission denied", e)
                409 -> ClientRequestExceptions.ConflictException("File already exists", e)
                else -> ClientRequestExceptions.InvalidRequestException("Upload failed: ${e.message}", e)
            }
        } catch (e: Exception) {
            logE(TAG, "Unexpected error uploading file", e)
            throw ClientRequestExceptions.InvalidRequestException("Upload failed: ${e.message}", e)
        }
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
        private const val BUCKET_NAME = "images"
    }
}
