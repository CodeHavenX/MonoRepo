package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.framework.core.CoreUri

interface StorageService {
    suspend fun uploadFile(data: ByteArray, targetRef: StorageRef): Result<StorageRef>

    suspend fun downloadImage(targetRef: StorageRef): Result<CoreUri>
}
