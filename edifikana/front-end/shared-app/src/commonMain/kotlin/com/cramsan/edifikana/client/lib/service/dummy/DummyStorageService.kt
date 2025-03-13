package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.framework.core.CoreUri
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Dummy implementation of [StorageService] that does nothing.
 */
class DummyStorageService : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String): Result<String> {
        delay(1.seconds)
        return Result.success("")
    }

    override suspend fun downloadFile(targetRef: String): Result<CoreUri> {
        delay(1.seconds)
        return Result.success(CoreUri.createUri(""))
    }
}
