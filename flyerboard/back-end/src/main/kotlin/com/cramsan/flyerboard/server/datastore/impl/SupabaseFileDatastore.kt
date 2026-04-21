package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.hours

/**
 * Supabase Storage implementation of [FileDatastore].
 *
 * Files are stored in the private `flyer-files` bucket. Each uploaded file is given a
 * UUID-prefixed name to prevent collisions between uploads with the same original filename.
 */
class SupabaseFileDatastore(
    private val storage: Storage,
) : FileDatastore {

    override suspend fun uploadFile(fileName: String, content: ByteArray): Result<String> =
        runSuspendCatching(TAG) {
            // Strip any directory components from the caller-supplied name to prevent path traversal.
            val baseName = fileName.substringAfterLast('/').substringAfterLast('\\')
            val path = "${UUID.random()}_$baseName"
            logD(TAG, "Uploading file: %s", path)
            storage.from(BUCKET).upload(path, content) {
                upsert = false
            }
            path
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
