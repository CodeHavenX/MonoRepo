package com.cramsan.edifikana.client.lib.utils

import android.provider.OpenableColumns
import com.cramsan.framework.core.CoreUri

/**
 * Android implementation for getting file size in bytes.
 */
internal actual fun getFileSizeBytesImpl(uri: CoreUri, dependencies: IODependencies): Result<Long> {
    return runCatching {
        val cursor = dependencies.contentResolver.query(
            uri.getAndroidUri(),
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    it.getLong(sizeIndex)
                } else {
                    throw IllegalStateException("Unable to get file size column")
                }
            } else {
                throw IllegalStateException("Unable to query file size")
            }
        } ?: throw IllegalStateException("Cursor is null")
    }
}

/**
 * Android implementation for getting MIME type.
 */
internal actual fun getMimeTypeImpl(uri: CoreUri, dependencies: IODependencies): Result<String> {
    return runCatching {
        val mimeType = dependencies.contentResolver.getType(uri.getAndroidUri())
        mimeType ?: "application/octet-stream" // Default if MIME type cannot be determined
    }
}
