@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.core.CoreUri

/**
 * Dummy implementation of [StorageService] for testing purposes.
 */
class DummyStorageService : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> {
        return Result.success(targetRef)
    }

    override suspend fun downloadImage(targetRef: String): Result<CoreUri> {
        return Result.success(CoreUri.createUri("https://via.placeholder.com/150"))
    }
}
