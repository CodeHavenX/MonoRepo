package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.core.CoreUri

/**
 * Dummy implementation of [StorageService] that does nothing.
 */
class DummyStorageService : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> {
        return Result.success("")
    }

    override suspend fun downloadFile(targetRef: String): Result<CoreUri> {
        return Result.success(CoreUri.createUri(""))
    }
}
