package com.cramsan.edifikana.client.desktop.service

import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.utils.runSuspendCatching
import com.cramsan.framework.core.CoreUri
import dev.gitlive.firebase.storage.FirebaseStorage

@Suppress("UnusedPrivateProperty")
class FirebaseStorageService(
    private val storage: FirebaseStorage,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: StorageRef): Result<StorageRef> = runSuspendCatching {
        TODO()
    }

    override suspend fun downloadImage(targetRef: StorageRef): Result<CoreUri> = runSuspendCatching {
        TODO()
    }

    companion object {
        private const val TAG = "FirebaseStorageService"
        private const val ONE_MEGABYTE = 1024 * 1024L
    }
}
