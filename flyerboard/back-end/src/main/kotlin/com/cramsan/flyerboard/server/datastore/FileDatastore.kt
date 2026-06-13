package com.cramsan.flyerboard.server.datastore

import com.cramsan.framework.annotations.BackendDatastore

/**
 * Interface defining file storage operations for flyer assets.
 */
@BackendDatastore
interface FileDatastore {
    /**
     * Generates a signed URL the client can use to upload the asset for [filePath] directly to
     * storage. The upload overwrites any existing object at [filePath].
     */
    suspend fun createSignedUploadUrl(filePath: String): Result<SignedUpload>

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

/**
 * A signed URL the client can use to upload a file directly to storage.
 */
data class SignedUpload(val signedUrl: String, val token: String)
