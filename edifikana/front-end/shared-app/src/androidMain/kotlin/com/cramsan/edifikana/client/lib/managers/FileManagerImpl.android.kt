package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.edifikana.client.lib.utils.getFilename
import com.cramsan.edifikana.client.lib.utils.processImageData
import com.cramsan.edifikana.client.lib.utils.readBytes
import com.cramsan.framework.core.CoreUri

/**
 * Android implementation of FileManager.
 *
 * Delegates to existing platform-specific utility functions in CorutilesUtils.android.kt.
 * This implementation:
 * - Reads files using Android ContentResolver
 * - Processes images with EXIF orientation correction
 * - Compresses images (JPEG at 35% quality)
 * - Extracts filenames from content:// URIs
 */
class FileManagerImpl(
    private val ioDependencies: IODependencies
) : FileManager {

    override suspend fun readFileBytes(uri: CoreUri): Result<ByteArray> {
        return readBytes(uri, ioDependencies)
    }

    override suspend fun processImage(data: ByteArray): Result<ByteArray> {
        // Android implementation includes:
        // - EXIF orientation correction
        // - Image compression (35% quality)
        return processImageData(data)
    }

    override fun getFilename(uri: CoreUri): String {
        return uri.getFilename(ioDependencies)
    }
}
