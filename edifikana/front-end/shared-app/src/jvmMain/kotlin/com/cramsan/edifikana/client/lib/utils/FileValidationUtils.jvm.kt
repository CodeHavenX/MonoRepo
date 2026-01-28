package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * JVM implementation for getting file size in bytes.
 */
internal actual fun getFileSizeBytesImpl(uri: CoreUri, dependencies: IODependencies): Result<Long> {
    return runCatching {
        val file = File(uri.getUri())
        if (file.exists()) {
            file.length()
        } else {
            throw IllegalStateException("File does not exist: ${uri.getUri()}")
        }
    }
}

/**
 * JVM implementation for getting MIME type.
 */
internal actual fun getMimeTypeImpl(uri: CoreUri, dependencies: IODependencies): Result<String> {
    return runCatching {
        val path = Paths.get(uri.getUri())
        val mimeType = Files.probeContentType(path)
        mimeType ?: "application/octet-stream" // Default if MIME type cannot be determined
    }
}
