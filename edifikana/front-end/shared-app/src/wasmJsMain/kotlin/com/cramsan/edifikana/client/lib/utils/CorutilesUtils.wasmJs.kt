package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri

/**
 * I/O dependencies.
 */
actual class IODependencies

/**
 * Read bytes from a URI.
 */
actual fun readBytes(
    uri: CoreUri,
    dependencies: IODependencies
): Result<ByteArray> {
    TODO("Not yet implemented")
}

/**
 * Process image data.
 */
actual fun processImageData(data: ByteArray): Result<ByteArray> {
    TODO("Not yet implemented")
}
