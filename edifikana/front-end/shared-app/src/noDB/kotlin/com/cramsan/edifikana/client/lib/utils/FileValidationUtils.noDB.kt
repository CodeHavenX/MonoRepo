package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri

/**
 * NoDB implementation for getting file size in bytes.
 * This is a stub implementation for platforms without database support (e.g., wasmJs).
 */
internal actual fun getFileSizeBytesImpl(uri: CoreUri, dependencies: IODependencies): Result<Long> {
    TODO("Not yet implemented for noDB platforms")
}

/**
 * NoDB implementation for getting MIME type.
 * This is a stub implementation for platforms without database support (e.g., wasmJs).
 */
internal actual fun getMimeTypeImpl(uri: CoreUri, dependencies: IODependencies): Result<String> {
    TODO("Not yet implemented for noDB platforms")
}
