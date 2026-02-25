package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.core.CoreUri

/**
 * Manager responsible for file I/O operations.
 *
 * This interface abstracts platform-specific file operations (reading, processing, etc.)
 * to allow ViewModels to focus on business logic without coupling to platform implementations.
 *
 * Platform-specific implementations handle:
 * - File reading from URIs
 * - Image processing (EXIF rotation, compression)
 * - Filename extraction from URIs
 */
interface FileService {

    /**
     * Read bytes from a file URI.
     *
     * @param uri The URI of the file to read
     * @return Result containing file bytes on success, or error on failure
     */
    suspend fun readFileBytes(uri: CoreUri): Result<ByteArray>

    /**
     * Process image data with platform-specific optimizations.
     *
     * Platform implementations may include:
     * - EXIF orientation correction
     * - Image compression
     * - Format conversion
     *
     * @param data Raw image bytes
     * @return Result containing processed bytes on success, or error on failure
     */
    suspend fun processImage(data: ByteArray): Result<ByteArray>

    /**
     * Extract filename from a URI.
     *
     * @param uri The URI to extract filename from
     * @return The filename string
     * @throws Exception if filename cannot be extracted
     */
    fun getFilename(uri: CoreUri): String
}
