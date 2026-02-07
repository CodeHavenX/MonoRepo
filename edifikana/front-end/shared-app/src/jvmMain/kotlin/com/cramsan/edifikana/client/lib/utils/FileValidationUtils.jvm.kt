package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * JVM implementation for getting file size in bytes.
 * Handles both file:// URIs and plain file paths.
 */
internal actual fun getFileSizeBytesImpl(uri: CoreUri, dependencies: IODependencies): Result<Long> {
    return runCatching {
        val uriString = uri.getUri()
        val file = if (uriString.startsWith("file:")) {
            File(URI(uriString))
        } else {
            File(uriString)
        }

        require(file.exists()) { "File does not exist: $uriString" }
        file.length()
    }
}

/**
 * JVM implementation for getting MIME type.
 * Handles both file:// URIs and plain file paths.
 */
internal actual fun getMimeTypeImpl(uri: CoreUri, dependencies: IODependencies): Result<String> {
    return runCatching {
        val uriString = uri.getUri()
        val path = if (uriString.startsWith("file:")) {
            Paths.get(URI(uriString))
        } else {
            Paths.get(uriString)
        }

        val mimeType = Files.probeContentType(path)
        mimeType ?: "application/octet-stream" // Default if MIME type cannot be determined
    }
}
