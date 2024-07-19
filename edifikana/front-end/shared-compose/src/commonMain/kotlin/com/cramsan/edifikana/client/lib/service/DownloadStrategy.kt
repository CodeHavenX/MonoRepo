package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.framework.core.CoreUri

interface DownloadStrategy {

    fun isFileCached(targetRef: StorageRef): Boolean

    fun getCachedFile(targetRef: StorageRef): CoreUri

    fun saveToFile(data: ByteArray, targetRef: StorageRef): CoreUri
}
