package com.cramsan.edifikana.server.core.datastore

import com.cramsan.edifikana.server.core.service.models.File
import com.cramsan.edifikana.server.core.service.models.requests.CreateFileRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest

/**
 * Interface for the storage datastore.
 */
interface StorageDatastore {
    /**
     * Creates a new file for the given [request]. Returns the [Result] of the operation with the created [File].
     */
    suspend fun createFile(
        request: CreateFileRequest,
    ): Result<File>

    /**
     * Retrieves a file for the given [request]. Returns the [Result] of the operation with the fetched [File] if found.
     */
    suspend fun getFile(
        request: GetFileRequest,
    ): Result<File?>
}