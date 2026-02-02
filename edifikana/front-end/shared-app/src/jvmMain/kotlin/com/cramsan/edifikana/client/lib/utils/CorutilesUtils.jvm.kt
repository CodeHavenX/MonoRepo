package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri
import java.io.File
import java.net.URI

/**
 * Utility class for CoreUri.
 */
actual class IODependencies

/**
 * Read the bytes from the given [uri].
 *
 * JVM implementation using standard File I/O.
 */
actual fun readBytes(
    uri: CoreUri,
    dependencies: IODependencies
): Result<ByteArray> = runCatching {
    val uriString = uri.getUri()
    val file = if (uriString.startsWith("file:")) {
        File(URI(uriString))
    } else {
        File(uriString)
    }

    require(file.exists()) { "File does not exist: $uriString" }
    require(file.canRead()) { "File is not readable: $uriString" }

    file.readBytes()
}

/**
 * Process image data (rotation correction, compression, etc.).
 *
 * TODO: JVM implementation is simplified - no image processing is performed.
 * This is a temporary solution to enable basic file upload functionality on JVM/Desktop.
 *
 * Full implementation should include:
 * - EXIF data reading for orientation correction
 * - Image rotation based on EXIF orientation
 * - Image compression (e.g., JPEG at 35% quality like Android)
 * - Consider using javax.imageio or libraries like TwelveMonkeys ImageIO
 *
 * Current behavior: Returns the raw image data without any processing.
 * This means images may appear rotated incorrectly and files will be larger than on Android.
 */
actual fun processImageData(data: ByteArray): Result<ByteArray> = runCatching {
    // TODO: Implement image processing for JVM (EXIF rotation, compression)
    // For now, return raw data to enable basic upload functionality
    data
}
