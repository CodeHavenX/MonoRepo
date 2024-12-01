package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri

/**
 * I/O dependencies.
 */
expect class IODependencies

/**
 * Read bytes from a URI.
 */
expect fun readBytes(uri: CoreUri, dependencies: IODependencies): Result<ByteArray>

/**
 * Process image data.
 */
expect fun processImageData(data: ByteArray): Result<ByteArray>
