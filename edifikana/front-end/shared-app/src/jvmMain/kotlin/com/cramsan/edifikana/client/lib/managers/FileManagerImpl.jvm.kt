package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.processImageData
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.framework.core.CoreUri

/**
 * JVM implementation of FileManager.
 *
 * Delegates to existing platform-specific utility functions in CorutilesUtils.jvm.kt.
 * This implementation:
 * - Reads files using standard Java File I/O
 * - Returns raw image data without processing (JVM limitation documented in CorutilesUtils)
 * - Extracts filenames from file:// URIs
 */
class FileManagerImpl(
    private val ioDependencies: IODependencies
) : FileManager {

    override suspend fun readFileBytes(uri: CoreUri): Result<ByteArray> {
        return readBytes(uri, ioDependencies)
    }

    override suspend fun processImage(data: ByteArray): Result<ByteArray> {
        // Note: JVM implementation currently returns raw data
        // See CorutilesUtils.jvm.kt for details on limitations
        return processImageData(data)
    }

    override fun getFilename(uri: CoreUri): String {
        return uri.getFilename(ioDependencies)
    }
}
