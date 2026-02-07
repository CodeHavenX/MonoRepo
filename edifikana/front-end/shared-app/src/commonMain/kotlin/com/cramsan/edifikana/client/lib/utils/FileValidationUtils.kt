package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri

/**
 * Maximum file size for uploads (10MB in bytes).
 */
const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L // 10MB

/**
 * Supported image MIME types for upload validation.
 */
val SUPPORTED_IMAGE_MIME_TYPES = setOf(
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/gif",
    "image/webp",
)

/**
 * Supported document MIME types for upload validation.
 */
val SUPPORTED_DOCUMENT_MIME_TYPES = setOf(
    "application/pdf",
    "application/msword", // .doc
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
    "text/plain", // .txt
)

/**
 * All supported MIME types (images + documents).
 */
val ALL_SUPPORTED_MIME_TYPES = SUPPORTED_IMAGE_MIME_TYPES + SUPPORTED_DOCUMENT_MIME_TYPES

/**
 * File validation utilities for checking file size and type before upload.
 */
object FileValidationUtils {

    /**
     * Validates that the file size is within the allowed limit.
     *
     * @param uri The file URI to validate
     * @param dependencies Platform-specific IO dependencies
     * @return Result.success if valid, Result.failure with error message if invalid
     */
    fun validateFileSize(uri: CoreUri, dependencies: IODependencies): Result<Unit> {
        return getFileSizeBytes(uri, dependencies).fold(
            onSuccess = { size ->
                if (size > MAX_FILE_SIZE_BYTES) {
                    val sizeMB = size / (1024.0 * 1024.0)
                    val maxSizeMB = MAX_FILE_SIZE_BYTES / (1024.0 * 1024.0)
                    Result.failure(
                        IllegalArgumentException(
                            "File size $sizeMB MB) exceeds maximum allowed size $maxSizeMB MB"
                        )
                    )
                } else {
                    Result.success(Unit)
                }
            },
            onFailure = { error ->
                Result.failure(IllegalArgumentException("Unable to determine file size: ${error.message}"))
            }
        )
    }

    /**
     * Validates that the file type is one of the supported MIME types.
     *
     * @param uri The file URI to validate
     * @param dependencies Platform-specific IO dependencies
     * @param imagesOnly If true, only accept image types; if false, accept all supported types
     * @return Result.success with MIME type if valid, Result.failure with error message if invalid
     */
    fun validateFileType(
        uri: CoreUri,
        dependencies: IODependencies,
        imagesOnly: Boolean = true
    ): Result<String> {
        return getMimeType(uri, dependencies).fold(
            onSuccess = { mimeType ->
                val supportedTypes = if (imagesOnly) SUPPORTED_IMAGE_MIME_TYPES else ALL_SUPPORTED_MIME_TYPES
                if (mimeType.lowercase() in supportedTypes) {
                    Result.success(mimeType)
                } else {
                    val allowedFormats = if (imagesOnly) {
                        "JPG, PNG, GIF, or WebP"
                    } else {
                        "JPG, PNG, GIF, WebP, PDF, DOC, DOCX, or TXT"
                    }
                    Result.failure(
                        IllegalArgumentException(
                            "Unsupported file type: $mimeType. Please select a valid $allowedFormats file."
                        )
                    )
                }
            },
            onFailure = { error ->
                Result.failure(IllegalArgumentException("Unable to determine file type: ${error.message}"))
            }
        )
    }

    /**
     * Gets the file size in bytes.
     * Platform-specific implementation required.
     *
     * @param uri The file URI
     * @param dependencies Platform-specific IO dependencies
     * @return Result containing file size in bytes
     */
    fun getFileSizeBytes(uri: CoreUri, dependencies: IODependencies): Result<Long> {
        return getFileSizeBytesImpl(uri, dependencies)
    }

    /**
     * Gets the MIME type of the file.
     * Platform-specific implementation required.
     *
     * @param uri The file URI
     * @param dependencies Platform-specific IO dependencies
     * @return Result containing MIME type string
     */
    fun getMimeType(uri: CoreUri, dependencies: IODependencies): Result<String> {
        return getMimeTypeImpl(uri, dependencies)
    }
}

/**
 * Platform-specific implementation for getting file size.
 * Must be implemented in androidMain and jvmMain source sets.
 */
internal expect fun getFileSizeBytesImpl(uri: CoreUri, dependencies: IODependencies): Result<Long>

/**
 * Platform-specific implementation for getting MIME type.
 * Must be implemented in androidMain and jvmMain source sets.
 */
internal expect fun getMimeTypeImpl(uri: CoreUri, dependencies: IODependencies): Result<String>
