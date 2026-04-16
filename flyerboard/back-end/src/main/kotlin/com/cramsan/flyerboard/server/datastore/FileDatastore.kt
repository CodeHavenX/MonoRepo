package com.cramsan.flyerboard.server.datastore

/**
 * Interface defining file storage operations for flyer assets.
 */
interface FileDatastore {

    /**
     * Uploads [content] to storage under a collision-safe name derived from [fileName].
     * Returns the stored file path (UUID-prefixed) on success.
     */
    suspend fun uploadFile(fileName: String, content: ByteArray): Result<String>

    /**
     * Generates a short-lived signed URL for the file at [filePath].
     * The URL is valid for 1 hour.
     */
    suspend fun getSignedUrl(filePath: String): Result<String>

    /**
     * Deletes the file at [filePath] from storage.
     */
    suspend fun deleteFile(filePath: String): Result<Unit>
}
