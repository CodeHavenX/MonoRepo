package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri

/**
 * Core URI utils for noDB platforms (WASM/browser).
 *
 * Extracts filename from URI string. In browser contexts, this works with
 * file:// URIs or blob: URIs from file input elements.
 *
 * Note: For blob URIs that don't contain filename information, generates
 * a timestamp-based name without extension (the actual file type should be
 * determined by MIME type validation elsewhere in the upload flow).
 */
actual fun CoreUri.getFilename(ioDependencies: IODependencies): String {
    val uriString = this.getUri()

    // Extract filename from URI path
    // Handle various URI formats: file://path/to/file.jpg, blob:http://..., or plain paths
    val filename = when {
        // For blob URIs, try to extract from the end of the URI
        uriString.startsWith("blob:") -> {
            // Blob URIs don't contain filenames, generate a timestamp-based name
            // Don't assume extension - let MIME type validation handle the actual format
            "upload_${System.currentTimeMillis()}"
        }
        // For file URIs or paths, extract the last segment (preserves original extension)
        else -> {
            uriString.substringAfterLast('/', "unknown_file")
                .substringAfterLast('\\', "unknown_file")
        }
    }

    return filename.ifEmpty { "unknown_file" }
}
