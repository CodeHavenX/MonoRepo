package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.FileId
import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.service.models.File
import com.cramsan.edifikana.server.core.service.models.requests.CreateFileRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetFileRequest
import com.cramsan.framework.configuration.TAG
import com.cramsan.framework.logging.logD

class StorageService(
    private val storageDatastore: StorageDatastore,
) {
    /**
     * Creates a file with the provided [fileName] and [content].
     */
    suspend fun createFile(
        fileName: String,
        content: ByteArray
    ): File {
        logD(TAG, "createFile")
        return storageDatastore.createFile(
            request = CreateFileRequest(
                fileName = fileName,
                content = content,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a file with the provided [id] if it exists.
     */
    suspend fun getFile(
        id: FileId,
    ): File? {
        logD(TAG, "getFile")
        val file = storageDatastore.getFile(
            request = GetFileRequest(
                id = id,
            ),
        ).getOrNull()

        return file
    }

    /**
     * Companion object for logging purposes.
     */
    companion object {
        private const val TAG = "StorageService"
    }
}