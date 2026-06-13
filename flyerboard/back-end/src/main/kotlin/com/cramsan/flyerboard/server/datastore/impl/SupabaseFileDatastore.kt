package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.SignedUpload
import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

/**
 * Supabase Storage implementation of [FileDatastore].
 *
 * Files are stored in the private `flyer-files` bucket, keyed by the flyer's own ID.
 */
@BackendDatastore
class SupabaseFileDatastore(private val storage: Storage) : FileDatastore {
    override suspend fun createSignedUploadUrl(filePath: String): Result<SignedUpload> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating signed upload URL for: %s", filePath)
            val signed = storage.from(BUCKET).createSignedUploadUrl(filePath, upsert = true)
            SignedUpload(signedUrl = signed.url, token = signed.token)
        }

    override suspend fun getSignedUrl(filePath: String): Result<String> =
        runSuspendCatching(TAG) {
            logD(TAG, "Generating signed URL for: %s", filePath)
            storage.from(BUCKET).createSignedUrl(filePath, expiresIn = 1.hours)
        }

    override suspend fun deleteFile(filePath: String): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Deleting file: %s", filePath)
            storage.from(BUCKET).delete(filePath)
        }

    companion object {
        private const val TAG = "SupabaseFileDatastore"
        private const val BUCKET = "flyer-files"
    }
}
