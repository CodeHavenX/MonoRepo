package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.core.CoreUri

/**
 * Download strategy for Wasm.
 */
class WasmDownloadStrategy : DownloadStrategy {

    override fun isFileCached(targetRef: String): Boolean {
        return false
    }

    override fun getCachedFile(targetRef: String): CoreUri {
        throw RuntimeException("Not supported")
    }

    override fun saveToFile(data: ByteArray, targetRef: String): CoreUri {
        throw RuntimeException("Not supported")
    }
}
